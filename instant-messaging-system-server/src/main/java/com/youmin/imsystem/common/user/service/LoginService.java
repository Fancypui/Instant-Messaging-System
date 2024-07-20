package com.youmin.imsystem.common.user.service;

import com.youmin.imsystem.common.user.domain.vo.resp.UserInfoResp;
import org.springframework.stereotype.Service;


public interface LoginService {
    /**
     * generate jwt token
     * @return
     */
    String login(Long uid);

    /**
     * renew jwt token if needed
     * @param token
     */
    void renewalTokenIfNecessary(String token);

    /**
     * Verify if token is valid
     * @param token
     * @return
     */
    Long getValidUid(String token);



}
