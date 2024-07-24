package com.youmin.imsystem.common.user.domain.entity;

import cn.hutool.core.util.StrUtil;
import lombok.Data;
import org.apache.velocity.util.StringUtils;

import java.io.Serializable;
import java.util.Objects;
import java.util.Optional;

@Data
public class IpInfo implements Serializable {

    private static final long serialVersionUID = 1L;

    //the ip during registration
    private String createIp;

    private IpDetail createIpDetail;

    //the latest ip
    private String updateIp;

    private IpDetail updateIpDetail;


    public void refreshIp(String ip){
        if(Objects.isNull(ip)){
            return;
        }
        updateIp = ip;
        if(createIp==null){
            createIp = ip;
        }
    }
    public String needRefreshIpDetail(){
        boolean noNeedRefresh =
                Optional.ofNullable(updateIpDetail)
                        .map(IpDetail::getIp)
                        .filter(ip->Objects.equals(ip,updateIp))
                        .isPresent();
        return noNeedRefresh?null:updateIp;
    }

    public void refreshDetail(IpDetail ipDetail) {
        if(Objects.equals(createIp,ipDetail.getIp())){
            createIpDetail = ipDetail;
        }
        if(Objects.equals(updateIp,ipDetail.getIp())){
            updateIpDetail = ipDetail;
        }

    }
}
