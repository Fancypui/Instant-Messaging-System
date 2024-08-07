package com.youmin.imsystem.common.user.service.impl;

import cn.hutool.core.lang.TypeReference;
import cn.hutool.core.thread.NamedThreadFactory;
import cn.hutool.http.HttpUtil;
import cn.hutool.json.JSONUtil;
import com.youmin.imsystem.common.common.domain.vo.resp.ApiResult;
import com.youmin.imsystem.common.common.handler.GlobalUncaughtExceptionHandler;
import com.youmin.imsystem.common.common.utils.JsonUtils;
import com.youmin.imsystem.common.user.cache.UserCache;
import com.youmin.imsystem.common.user.cache.UserInfoCache;
import com.youmin.imsystem.common.user.dao.UserDao;
import com.youmin.imsystem.common.user.domain.dto.IpResult;
import com.youmin.imsystem.common.user.domain.entity.IpDetail;
import com.youmin.imsystem.common.user.domain.entity.IpInfo;
import com.youmin.imsystem.common.user.domain.entity.User;
import com.youmin.imsystem.common.user.service.IpService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.Objects;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * Service that update location info of user based on ip address
 */
@Slf4j
@Service
public class IpServiceImpl implements IpService {

    private static final ExecutorService EXECUTOR = new ThreadPoolExecutor(1, 1,
            0L, TimeUnit.MILLISECONDS,
            new LinkedBlockingQueue<>(500),
            new NamedThreadFactory("refresh-ipDetail", null, false,
                    GlobalUncaughtExceptionHandler.getInstance()));


    @Autowired
    private UserDao userDao;

    @Autowired
    private UserCache userCache;

    @Override
    public void refreshIpDetail(Long uid) {
        EXECUTOR.execute(()->{
            User user = userDao.getById(uid);
            IpInfo ipInfo = user.getIpInfo();
            if(Objects.isNull(ipInfo)){
                return;
            }
            String ip = ipInfo.needRefreshIpDetail();
            if(Objects.isNull(ip)){
                return;
            }
            IpDetail ipDetail = tryGetIpDetailThreeTimeOrNull(ipInfo.getUpdateIp());
            //update user location based on ip if ipDetail is obtained
            if(Objects.nonNull(ipDetail)){
                User update = new User();
                ipInfo.refreshDetail(ipDetail);
                update.setId(user.getId());
                update.setIpInfo(ipInfo);
                userDao.updateById(update);
                userCache.userInfoChange(uid);
            }
        });

    }


    private  IpDetail tryGetIpDetailThreeTimeOrNull(String ip) {
        //try to get data three time using taobao ip resolve api
        for (int i = 0; i < 3; i++) {
            IpDetail ipDetail = getIpDetail(ip);
            if(Objects.nonNull(ipDetail)){
                return ipDetail;
            }
            try {
                Thread.sleep(2000);
            } catch (InterruptedException e) {
                log.error("tryGetIpDetailThreeTimeOrNull InterruptedException: {}",e);
            }
        }
        return null;
    }

    /**
     * Use taobao ip resolve api to get user location
     * @param ip
     * @return
     */
    private IpDetail getIpDetail(String ip){
        String url =
                "https://ip.taobao.com/outGetIpInfo?ip="+ip+"&accessKey=alibaba-inc";
        //call api
        String body = HttpUtil.get(url);
        try {
            //convert response body to IpDetail
            IpResult<IpDetail> result = JSONUtil.toBean(body, new TypeReference<IpResult<IpDetail>>() {
            }, false);
            if (result.isSuccess()) {
                return result.getData();
            }
        }catch (Exception e){

        }
        return null;
    }




}
