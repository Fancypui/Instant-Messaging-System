package com.youmin.imsystem.common;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.youmin.imsystem.common.common.domain.dto.PushMessageDTO;
import com.youmin.imsystem.common.user.domain.vo.resp.WSFriendApply;
import com.youmin.imsystem.common.user.domain.vo.resp.WSRespBase;


public class test {

    public static void main(String[] args) throws JsonProcessingException {
        ObjectMapper objectMapper = new ObjectMapper();
        WSFriendApply wsFriendApply = new WSFriendApply(1L, 1);
        WSRespBase<WSFriendApply> wsFriendApplyWSRespBase = new WSRespBase<>();
        wsFriendApplyWSRespBase.setType(1);
        wsFriendApplyWSRespBase.setData(wsFriendApply);

        String s = objectMapper.writeValueAsString(wsFriendApplyWSRespBase);
        System.out.println(s);
        WSRespBase<WSFriendApply> wsFriendApply1 = objectMapper.readValue(s, WSRespBase.class);
        System.out.println(wsFriendApply1.getData());

    }
}
