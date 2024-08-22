package com.youmin.imsystem.common.common.event;

import com.youmin.imsystem.common.chat.domain.dto.MsgRecallDTO;
import com.youmin.imsystem.common.chat.domain.entity.Message;
import com.youmin.imsystem.common.user.domain.entity.UserApply;
import lombok.Getter;
import org.springframework.context.ApplicationEvent;

@Getter
public class MsgRecallEvent extends ApplicationEvent {
    private MsgRecallDTO msgRecallDTO;

    public MsgRecallEvent(Object source, MsgRecallDTO msgRecallDTO) {
        super(source);
        this.msgRecallDTO = msgRecallDTO;
    }
}
