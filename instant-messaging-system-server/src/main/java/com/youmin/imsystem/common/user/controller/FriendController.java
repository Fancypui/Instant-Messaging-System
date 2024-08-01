package com.youmin.imsystem.common.user.controller;

import com.youmin.imsystem.common.common.domain.vo.req.CursorBaseReq;
import com.youmin.imsystem.common.common.domain.vo.resp.ApiResult;
import com.youmin.imsystem.common.common.domain.vo.resp.CursorPageBaseResp;
import com.youmin.imsystem.common.common.utils.RequestHolder;
import com.youmin.imsystem.common.user.domain.vo.resp.FriendResp;
import com.youmin.imsystem.common.user.service.FriendService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;

@RestController
@RequestMapping("/capi/user/friend")
@Api(tags = "Friend Module")
public class FriendController {

    @Autowired
    private FriendService friendService;

    @GetMapping("/page")
    @ApiOperation("friend list")
    public ApiResult<CursorPageBaseResp<FriendResp>> friendPage(@Valid CursorBaseReq friendPageReq){
        Long uid = RequestHolder.get().getUid();
        return ApiResult.success(friendService.friendList(friendPageReq,uid));
    }

}
