package com.youmin.imsystem.common.chat.service.strategy.mark;

import com.youmin.imsystem.common.chat.domain.enums.MessageMarkTypeEnum;
import org.springframework.stereotype.Component;

@Component
public class DisLikeMarkStrategy extends AbstractMessageMark {
    @Override
    public MessageMarkTypeEnum getMarkType() {
        return MessageMarkTypeEnum.DISLIKE;
    }

    @Override
    public void doMark(Long uid, Long msgId) {
        super.doMark(uid, msgId);
        //simultenously cancel the like records
        MessageMarkStrategyFactory.getStrategyOrNull(MessageMarkTypeEnum.LIKE.getType()).unmark(uid,msgId);
    }
}
