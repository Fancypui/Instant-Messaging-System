package com.youmin.imsystem.common.chat.service.strategy.msg;

import cn.hutool.core.collection.CollectionUtil;
import com.youmin.imsystem.common.chat.dao.GroupMemberDao;
import com.youmin.imsystem.common.chat.dao.MessageDao;
import com.youmin.imsystem.common.chat.domain.entity.Message;
import com.youmin.imsystem.common.chat.domain.entity.RoomGroup;
import com.youmin.imsystem.common.chat.domain.entity.msg.MessageExtra;
import com.youmin.imsystem.common.chat.domain.enums.MessageTypeEnum;
import com.youmin.imsystem.common.chat.domain.vo.request.msg.TextMsgReq;
import com.youmin.imsystem.common.chat.service.cache.RoomGroupCache;
import com.youmin.imsystem.common.common.utils.AssertUtils;
import com.youmin.imsystem.common.user.cache.UserInfoCache;
import com.youmin.imsystem.common.user.domain.entity.User;
import com.youmin.imsystem.common.user.enums.RoleEnum;
import com.youmin.imsystem.common.user.service.IRoleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

@Component
public class TextMsgHandler extends AbstractMsgHandler<TextMsgReq>{

    @Autowired
    private MessageDao messageDao;

    @Autowired
    private RoomGroupCache roomGroupCache;

    @Autowired
    private UserInfoCache userInfoCache;
    @Autowired
    private IRoleService roleService;

    @Override
    protected void checkMsg(TextMsgReq body, Long roomId, Long uid) {
        //validate reply message
        if(Objects.nonNull(body.getReplyMsgId())){
            Message replyMessage = messageDao.getById(body.getReplyMsgId());
            AssertUtils.isNotEmpty(replyMessage,"Replied Message not found");
            AssertUtils.equal(replyMessage.getRoomId(),roomId,"You only allow to reply the message in the same contact session");
        }
        if(CollectionUtil.isNotEmpty(body.getAtUidList())){
            //remove duplications
            List<Long> atUidList = body.getAtUidList().stream().distinct().collect(Collectors.toList());
            RoomGroup roomGroup = roomGroupCache.get(roomId);
            Map<Long, User> batch = userInfoCache.getBatch(atUidList);
            //if tagged uid not found, but userInfoCache will still return null, need to filter
            long batchCount = batch.values().stream().filter(Objects::isNull).count();
            AssertUtils.equal(batchCount,(long)atUidList.size(),"@user not found");
            boolean atAll = body.getAtUidList().contains(0L);
            if(atAll){
                AssertUtils.isTrue(roleService.hasPower(uid, RoleEnum.CHAT_MANAGER),"No Authorized to tag all group member");
            }

        }

    }

    @Override
    protected void saveMsg(Message message, TextMsgReq body) {
        MessageExtra msgExtra = Optional.ofNullable(message.getExtra()).orElse(new MessageExtra());
        Message update = new Message();
        update.setId(message.getId());
        update.setExtra(msgExtra);
        update.setContent(body.getContent());
        //if got reply id
        if(Objects.nonNull(body.getReplyMsgId())){
            Integer gapCount = messageDao.getGapCount(message.getRoomId(),body.getReplyMsgId(),message.getRoomId());
            update.setReplyMsgId(body.getReplyMsgId());
            update.setGapCount(gapCount);
        }
        //todo text to url
        //tag feature
        if(CollectionUtil.isNotEmpty(body.getAtUidList())){
            msgExtra.setAtUidList(body.getAtUidList());
        }
        messageDao.updateById(update);
    }

    @Override
    MessageTypeEnum getMsgTypeEnum() {
        return MessageTypeEnum.TEXT;
    }
}
