package com.youmin.imsystem.common.common.event;

import com.youmin.imsystem.common.chat.domain.dto.ChatMessageMarkDTO;
import com.youmin.imsystem.common.chat.domain.entity.GroupMember;
import com.youmin.imsystem.common.chat.domain.entity.Room;
import com.youmin.imsystem.common.chat.domain.entity.RoomGroup;
import lombok.Getter;
import org.springframework.context.ApplicationEvent;

import java.util.List;

@Getter
public class GroupMemberAddEvent extends ApplicationEvent {

    private RoomGroup roomGorup;

    private List<GroupMember> groupMembers;

    private Long uid;


    public GroupMemberAddEvent(Object source, List<GroupMember> groupMembers, Long uid, RoomGroup roomGroup) {
        super(source);
        this.uid = uid;
        this.groupMembers = groupMembers;
        this.roomGorup = roomGroup;
    }
}
