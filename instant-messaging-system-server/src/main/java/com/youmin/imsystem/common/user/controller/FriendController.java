package com.youmin.imsystem.common.user.controller;

import com.youmin.imsystem.common.common.domain.vo.req.CursorBaseReq;
import com.youmin.imsystem.common.common.domain.vo.req.PageBaseReq;
import com.youmin.imsystem.common.common.domain.vo.resp.ApiResult;
import com.youmin.imsystem.common.common.domain.vo.resp.CursorPageBaseResp;
import com.youmin.imsystem.common.common.domain.vo.resp.PageBaseResp;
import com.youmin.imsystem.common.common.utils.RequestHolder;
import com.youmin.imsystem.common.user.domain.vo.req.FriendApplyApproveReq;
import com.youmin.imsystem.common.user.domain.vo.req.FriendApplyReq;
import com.youmin.imsystem.common.user.domain.vo.req.FriendCheckReq;
import com.youmin.imsystem.common.user.domain.vo.req.FriendDeleteReq;
import com.youmin.imsystem.common.user.domain.vo.resp.FriendApplyResp;
import com.youmin.imsystem.common.user.domain.vo.resp.FriendCheckResp;
import com.youmin.imsystem.common.user.domain.vo.resp.FriendResp;
import com.youmin.imsystem.common.user.domain.vo.resp.FriendUnreadResp;
import com.youmin.imsystem.common.user.service.FriendService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@RestController
@RequestMapping("/capi/user/friend")
@Api(tags = "Friend Module")
public class FriendController {

    @Autowired
    private FriendService friendService;

    //done
    @GetMapping("/page")
    @ApiOperation("friend list")
    public ApiResult<CursorPageBaseResp<FriendResp>> friendPage(@Valid CursorBaseReq friendPageReq){
        Long uid = RequestHolder.get().getUid();
        return ApiResult.success(friendService.friendList(friendPageReq,uid));
    }

    //done
    @GetMapping("/check")
    @ApiOperation("Batch check if provided uid is the friend of the current user")
    public ApiResult<FriendCheckResp> friendCheck(@Valid FriendCheckReq friendCheckReq){
        Long uid = RequestHolder.get().getUid();
        return ApiResult.success(friendService.friendCheck(friendCheckReq,uid));
    }

    //done
    @PostMapping("/apply")
    @ApiOperation("Send a Friend Request to target user")
    public ApiResult<Void> sendFriendRequest(@Valid @RequestBody FriendApplyReq request){
        Long uid = RequestHolder.get().getUid();
        friendService.sendFriendRequest(request,uid);
        return ApiResult.success();
    }

    //done
    @GetMapping("/unread")
    @ApiOperation("Send a Friend Request to target user")
    public ApiResult<FriendUnreadResp> unread(){
        Long uid = RequestHolder.get().getUid();
        return ApiResult.success(friendService.unread(uid));
    }

    //done
    @GetMapping("/apply/page")
    @ApiOperation("Get current user's Apply Page")
    public ApiResult<PageBaseResp<FriendApplyResp>> applyPage(@Valid PageBaseReq request){
        Long uid = RequestHolder.get().getUid();
        return ApiResult.success(friendService.applyPage(request,uid));
    }


    //done
    @PutMapping("/apply")
    @ApiOperation("Apply a friend request")
    public ApiResult<Void> applyApprove(@Valid @RequestBody FriendApplyApproveReq request){
        Long uid = RequestHolder.get().getUid();
        friendService.applyApprove(request,uid);
        return ApiResult.success();
    }

    @DeleteMapping("")
    @ApiOperation("Delete Friend")
    public ApiResult<Void> deleteFriend(@Valid @RequestBody FriendDeleteReq request){
        Long uid = RequestHolder.get().getUid();
        friendService.deleteFriend(request,uid);
        return ApiResult.success();
    }


}
