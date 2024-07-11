package com.youmin.imsystem.common.user.config;

import cn.hutool.json.JSONUtil;
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

import java.util.List;

@Data
@ConfigurationProperties(prefix = "wx.mp")
public class WxMpProperties {

    //to tell if using redis to store token
    private boolean useRedis;



    /**
     * multiple wechat official account config
     */
    private List<MpConfig> configs;

    @Data
    public static class MpConfig{

        /**
         * wechat official account appid
         */
        private String appId;
        /**
         * app secret
         */
        private String secret;
        /**
         * token
         */
        private String token;
        /**
         * EncodingAesKey
         */
        private String aesKey;
    }

    @Override
    public String toString() {
        return JSONUtil.toJsonStr(this);
    }
}
