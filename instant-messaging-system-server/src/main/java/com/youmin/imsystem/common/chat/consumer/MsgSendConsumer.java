package com.youmin.imsystem.common.chat.consumer;



import com.rabbitmq.client.Channel;
import com.youmin.imsystem.common.chat.dao.ContactDao;
import com.youmin.imsystem.common.chat.dao.MessageDao;
import com.youmin.imsystem.common.chat.dao.RoomDao;
import com.youmin.imsystem.common.chat.dao.RoomFriendDao;
import com.youmin.imsystem.common.chat.domain.entity.Message;
import com.youmin.imsystem.common.chat.domain.entity.Room;
import com.youmin.imsystem.common.chat.domain.entity.RoomFriend;
import com.youmin.imsystem.common.chat.domain.enums.RoomTypeEnums;
import com.youmin.imsystem.common.chat.domain.vo.response.ChatMessageResp;
import com.youmin.imsystem.common.chat.service.ChatService;
import com.youmin.imsystem.common.chat.service.cache.GroupMemberCache;
import com.youmin.imsystem.common.chat.service.cache.HotRoomCache;
import com.youmin.imsystem.common.chat.service.cache.RoomCache;
import com.youmin.imsystem.common.common.constant.MQConstant;
import com.youmin.imsystem.common.common.domain.dto.MsgSendMessageDTO;
import com.youmin.imsystem.common.user.enums.WSRespTypeEnum;
import com.youmin.imsystem.common.user.service.adapter.WSAdapter;
import com.youmin.imsystem.common.user.service.impl.PushService;
import com.youmin.imsystem.common.utils.JsonUtils;

import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

/**
 * consumer for processing message send event
 * Update room message box, and sync to room's member message box
 */
@Component
@Slf4j
public class MsgSendConsumer {

    @Autowired
    private MessageDao messageDao;

    @Autowired
    private ChatService chatService;
    @Autowired
    private RoomCache roomCache;

    @Autowired
    private RoomDao roomDao;

    @Autowired
    private GroupMemberCache groupMemberCache;

    @Autowired
    private RoomFriendDao roomFriendDao;

    @Autowired
    private ContactDao contactDao;
    @Autowired
    private PushService pushService;

    @Autowired
    private HotRoomCache hotRoomCache;

    @RabbitListener(queues = MQConstant.SEND_MSG_QUEUE)
    public void processNewMessage(Channel channel, org.springframework.amqp.core.Message message){
        log.info("COnsuming new message");
        MsgSendMessageDTO sendMsgDTO = JsonUtils.toObj(new String(message.getBody()), MsgSendMessageDTO.class);
        Message messageRecord = messageDao.getById(sendMsgDTO.getMsgId());
        ChatMessageResp msgResp = chatService.getMsgResp(messageRecord, null);
        Room room = roomCache.get(msgResp.getMessage().getRoomId());
        //update room with latest msgid and that corresponding msg send time
        roomDao.refreshActiveTime(messageRecord.getRoomId(),messageRecord.getId(),messageRecord.getCreateTime());
        roomCache.delete(room.getId());
        if(room.isHot()){//if it is hot room, push that message to all online users
            //update hot room latest message sent time in redis
            hotRoomCache.refreshActiveTime(room.getId(),messageRecord.getCreateTime());
            //push to all online users
            pushService.pushService(WSAdapter.buildMsgSend(msgResp));
        }else{
            List<Long> memberUidList = new ArrayList<>();
            if(Objects.equals(room.getType(), RoomTypeEnums.Group_Room.getRoomType())){//normal group chat - push to all member
                memberUidList = groupMemberCache.getMemberList(room.getId());
            }else if(Objects.equals(room.getType(), RoomTypeEnums.FRIEND_ROOM.getRoomType())){//friend room chat

                RoomFriend roomFriend = roomFriendDao.getByRoomId(room.getId());
                memberUidList = Arrays.asList(roomFriend.getUid1(),roomFriend.getUid2());
            }
            //update the contact session of all member
            contactDao.refreshOrCreateActiveTime(room.getId(),memberUidList,messageRecord.getId(),messageRecord.getCreateTime());
            //push to room member
            pushService.pushService(WSAdapter.buildMsgSend(msgResp),memberUidList);
        }
    }
}
