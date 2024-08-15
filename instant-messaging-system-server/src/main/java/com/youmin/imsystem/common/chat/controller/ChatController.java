package com.youmin.imsystem.common.chat.controller;

import com.youmin.imsystem.common.chat.domain.vo.request.ChatMessageReq;
import com.youmin.imsystem.common.chat.service.ChatService;
import com.youmin.imsystem.common.common.domain.vo.resp.ApiResult;
import com.youmin.imsystem.common.common.utils.RequestHolder;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;

@RestController()
@RequestMapping("/capi/chat")
@Api(tags = "Chat related apis")
public class ChatController {

    @Autowired
    private ChatService chatService;

    @ApiOperation("send message endpoint")
    @PostMapping("/msg")
    public ApiResult<?> sendMsg(@Valid @RequestBody ChatMessageReq request){
        Long msgId = chatService.sendMsg(request, RequestHolder.get().getUid());
        return ApiResult.success(chatService.getMsgResp(msgId, RequestHolder.get().getUid()));
    }
}
