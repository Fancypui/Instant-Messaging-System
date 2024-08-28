package com.youmin.imsystem.common.user.service.impl;

import com.youmin.imsystem.common.common.utils.AssertUtils;
import com.youmin.imsystem.common.user.domain.vo.req.UploadUrlReq;
import com.youmin.imsystem.common.user.enums.SceneEnum;
import com.youmin.oss.MinIoTemplate;
import com.youmin.oss.domain.OssReq;
import com.youmin.oss.domain.OssResp;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class OssServiceImpl {

    @Autowired
    private MinIoTemplate minIoTemplate;

    public OssResp getUploadUrl(UploadUrlReq request, Long uid) {
        SceneEnum sceneEnum = SceneEnum.of(request.getSceneId());
        AssertUtils.isNotEmpty(sceneEnum, "scene error");
        return minIoTemplate.getPresignedUrl(OssReq.builder()
                .uid(uid)
                .filename(request.getFileName())
                .filePath(sceneEnum.getPath()).build());
    }
}
