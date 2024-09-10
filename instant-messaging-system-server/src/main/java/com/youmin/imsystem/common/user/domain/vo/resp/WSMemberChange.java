package com.youmin.imsystem.common.user.domain.vo.resp;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class WSMemberChange {

    public static final Integer CHANGE_TYPE_ADD = 1;
    public static  final Integer CHANGE_TYPE_REMOVE = 2;


    private Long roomId;

    private Long uid;

    private Integer changeType;

    private Integer activeStatus;

    private Date lastOptTome;
}
