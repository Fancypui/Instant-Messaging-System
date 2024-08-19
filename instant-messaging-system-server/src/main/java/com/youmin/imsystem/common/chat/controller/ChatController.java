package com.youmin.imsystem.common.chat.controller;

import com.youmin.imsystem.common.chat.domain.vo.request.ChatMessagePageRequest;
import com.youmin.imsystem.common.chat.domain.vo.request.ChatMessageReq;
import com.youmin.imsystem.common.chat.domain.vo.response.ChatMessageResp;
import com.youmin.imsystem.common.chat.service.ChatService;
import com.youmin.imsystem.common.common.domain.vo.resp.ApiResult;
import com.youmin.imsystem.common.common.domain.vo.resp.CursorPageBaseResp;
import com.youmin.imsystem.common.common.utils.RequestHolder;
import com.youmin.imsystem.common.user.cache.UserCache;
import com.youmin.imsystem.common.user.enums.BlackTypeEnum;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

@RestController()
@RequestMapping("/capi/chat")
@Api(tags = "Chat related apis")
public class ChatController {

    @Autowired
    private ChatService chatService;

    @Autowired
    private UserCache userCache;

    @ApiOperation("send message endpoint")
    @PostMapping("/msg")
    public ApiResult<ChatMessageResp> sendMsg(@Valid @RequestBody ChatMessageReq request){
        Long msgId = chatService.sendMsg(request, RequestHolder.get().getUid());
        return ApiResult.success(chatService.getMsgResp(msgId, RequestHolder.get().getUid()));
    }

    @ApiOperation("Get message page from a room")
    @GetMapping("/msg/page")
    public ApiResult<CursorPageBaseResp<ChatMessageResp>> getMsgPage(@Valid ChatMessagePageRequest request){
        CursorPageBaseResp<ChatMessageResp> msgPage = chatService.getMsgPage(request, RequestHolder.get().getUid());
        filterMsgPage(msgPage.getList());
        return ApiResult.success(msgPage);
    }

    private void filterMsgPage(List<ChatMessageResp> list) {
        Set<String> uidBlackList = userCache.blackMap().getOrDefault(BlackTypeEnum.UID.getType(), new HashSet<>());
        list.removeIf(msgResp -> uidBlackList.contains(msgResp.getFromUser().getUid().toString()));
    }


}
