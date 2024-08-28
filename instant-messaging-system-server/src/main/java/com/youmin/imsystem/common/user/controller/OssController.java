package com.youmin.imsystem.common.user.controller;

import com.youmin.imsystem.common.common.domain.vo.resp.ApiResult;
import com.youmin.imsystem.common.common.utils.RequestHolder;
import com.youmin.imsystem.common.user.domain.vo.req.UploadUrlReq;
import com.youmin.imsystem.common.user.service.impl.OssServiceImpl;
import com.youmin.oss.domain.OssResp;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;

@RestController
@RequestMapping("/capi/oss")
@Api(tags="Oss Module")
public class OssController {

    @Autowired
    private OssServiceImpl ossService;

    @GetMapping("/upload/url")
    @ApiOperation("get upload url")
    public ApiResult<OssResp> getUploadUrl(@Valid UploadUrlReq request){
        return ApiResult.success(ossService.getUploadUrl(request, RequestHolder.get().getUid()));

    }
    /**
     * "uploadUrl": "http://localhost:9000/imsystem//chat/2024-08/11003/8c30aacc-fc49-4241-b2dc-da238cf1ac07.png?X-Amz-Algorithm=AWS4-HMAC-SHA256&X-Amz-Credential=FU8pcq1bzAEb7pAV%2F20240822%2Fus-east-1%2Fs3%2Faws4_request&X-Amz-Date=20240822T150114Z&X-Amz-Expires=86400&X-Amz-SignedHeaders=host&X-Amz-Signature=cca465bee1a8fdafd4a7c7b069bad4b54ca6b707b280e49a72373882380a5595",
     *         "downloadUrl": "http://localhost:9000/imsystem/chat/2024-08/11003/8c30aacc-fc49-4241-b2dc-da238cf1ac07.png"
     */
}
