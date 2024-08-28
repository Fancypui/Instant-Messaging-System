package com.youmin.imsystem.common.chat.service.strategy.mark;

import com.youmin.imsystem.common.chat.domain.enums.MessageMarkTypeEnum;
import org.springframework.stereotype.Component;

@Component
public class LikeMarkStrategy extends AbstractMessageMark {
    @Override
    public MessageMarkTypeEnum getMarkType() {
        return MessageMarkTypeEnum.LIKE;
    }

    @Override
    public void doMark(Long uid, Long msgId) {
        super.doMark(uid, msgId);
        //simultenously cancel the dislike records
        MessageMarkStrategyFactory.getStrategyOrNull(MessageMarkTypeEnum.DISLIKE.getType()).unmark(uid,msgId);
    }
}
