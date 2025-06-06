package com.youmin.imsystem.common.common.event;

import com.youmin.imsystem.common.chat.domain.dto.ChatMessageMarkDTO;
import lombok.Getter;
import org.springframework.context.ApplicationEvent;

@Getter
public class MessageMarkEvent extends ApplicationEvent {
    private ChatMessageMarkDTO dto;
    public MessageMarkEvent(Object source,ChatMessageMarkDTO dto) {
        super(source);
        this.dto = dto;
    }
}
