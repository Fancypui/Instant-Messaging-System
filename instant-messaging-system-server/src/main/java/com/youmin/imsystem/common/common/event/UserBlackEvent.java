package com.youmin.imsystem.common.common.event;

import com.youmin.imsystem.common.user.domain.entity.User;
import lombok.Getter;
import org.springframework.context.ApplicationEvent;

@Getter
public class UserBlackEvent extends ApplicationEvent {

    private User user;

    public UserBlackEvent(Object source, User user) {
        super(source);
        this.user = user;
    }
}
