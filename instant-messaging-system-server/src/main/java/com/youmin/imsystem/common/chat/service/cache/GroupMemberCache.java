package com.youmin.imsystem.common.chat.service.cache;

import com.youmin.imsystem.common.chat.dao.GroupMemberDao;
import com.youmin.imsystem.common.chat.dao.RoomGroupDao;
import com.youmin.imsystem.common.chat.domain.entity.GroupMember;
import com.youmin.imsystem.common.chat.domain.entity.RoomGroup;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Objects;

@Component
public class GroupMemberCache {

    @Autowired
    private GroupMemberDao groupMemberDao;

    @Autowired
    private RoomGroupDao roomGroupDao;

    @Cacheable(cacheNames = "member",key = "'groupMember'+#roomId")
    public List<Long> getMemberList(Long roomId){
        RoomGroup roomGroup = roomGroupDao.getByRoomId(roomId);
        if(Objects.isNull(roomGroup)){
            return null;
        }
        return groupMemberDao.getMemberUidList(roomGroup.getId());
    }
}
