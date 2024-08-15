package com.youmin.imsystem.common.chat.service.adapter;

import cn.hutool.core.bean.BeanUtil;
import com.youmin.imsystem.common.chat.domain.entity.Message;
import com.youmin.imsystem.common.chat.domain.entity.MessageMark;
import com.youmin.imsystem.common.chat.domain.enums.MessageMarkTypeEnum;
import com.youmin.imsystem.common.chat.domain.enums.MessageStatusEnum;
import com.youmin.imsystem.common.chat.domain.vo.request.ChatMessageReq;
import com.youmin.imsystem.common.chat.domain.vo.response.ChatMessageResp;
import com.youmin.imsystem.common.chat.service.strategy.msg.AbstractMsgHandler;
import com.youmin.imsystem.common.chat.service.strategy.msg.MsgHandlerFactory;
import com.youmin.imsystem.common.common.domain.enums.YesOrNoEnum;

import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

public class MessageAdapter {

    public static final Integer CAN_CALLBACK_GAP_COUNT = 100;

    public static Message buildMessageSave(ChatMessageReq request, Long uid){
        Message message = new Message();
        message.setRoomId(request.getRoomId());
        message.setFromUid(uid);
        message.setType(request.getMsgType());
        message.setStatus(MessageStatusEnum.NORMAL.getStatus());
        return message;
    }

    public static List<ChatMessageResp> buildMsgResp(List<Message> messages, List<MessageMark> messageMarks, Long receiveUid) {

        Map<Long, List<MessageMark>> markMap = messageMarks.stream().collect(Collectors.groupingBy(MessageMark::getMsgId));
        return messages.stream().map(message -> {
            ChatMessageResp resp = new ChatMessageResp();
            resp.setFromUser(buildFromUser(message.getFromUid()));
            resp.setMessage(buildMessage(message,markMap.getOrDefault(message.getId(),new ArrayList<>()),receiveUid));
            return resp;

        })
                .sorted(Comparator.comparing(a->a.getMessage() //sort message list in ascending order for frontend based on sendTime
                        .getSendTime())).collect(Collectors.toList());
    }

    private static ChatMessageResp.Message buildMessage(Message message, List<MessageMark> messageMarks, Long receiverUid){
        ChatMessageResp.Message messageVO = new ChatMessageResp.Message();
        BeanUtil.copyProperties(message,messageVO);
        messageVO.setSendTime(message.getCreateTime());
        AbstractMsgHandler<?> msgHandler = MsgHandlerFactory.getStrategyOrNull(message.getType());
        if(Objects.nonNull(msgHandler)){
            messageVO.setBody(msgHandler.showMsg(message));
        }
        //set messageMark
        messageVO.setMessageMark(buildMsgMark(messageMarks,receiverUid));
        return messageVO;
    }

    private static ChatMessageResp.MessageMark buildMsgMark(List<MessageMark> messageMarks, Long receiverUid) {
        Map<Integer, List<MessageMark>> typeMap = messageMarks.stream().collect(Collectors.groupingBy(MessageMark::getType));
        List<MessageMark> likeMarks = typeMap.getOrDefault(MessageMarkTypeEnum.LIKE.getType(), new ArrayList<>());
        List<MessageMark> disLikeMarks = typeMap.getOrDefault(MessageMarkTypeEnum.DISLIKE.getType(), new ArrayList<>());
        ChatMessageResp.MessageMark messageMark = new ChatMessageResp.MessageMark();
        messageMark.setLikeCount(likeMarks.size());
        messageMark.setDislikeCount(disLikeMarks.size());
        messageMark.setUserLike(Optional.ofNullable(receiverUid)
                .filter(uid->likeMarks.stream().anyMatch(a->Objects.equals(a.getUid(),receiverUid)))
                .map(a->YesOrNoEnum.YES.getStatus())
                .orElse(YesOrNoEnum.NO.getStatus()));
        messageMark.setDisLike(Optional.ofNullable(receiverUid)
                .filter(uid->disLikeMarks.stream().anyMatch(a->Objects.equals(a.getUid(),receiverUid)))
                .map(a->YesOrNoEnum.YES.getStatus())
                .orElse(YesOrNoEnum.NO.getStatus()));
        return messageMark;
    }

    private static ChatMessageResp.UserInfo buildFromUser(Long fromUid) {
        ChatMessageResp.UserInfo userInfo = new ChatMessageResp.UserInfo();
        userInfo.setUid(fromUid);
        return userInfo;
    }
}
