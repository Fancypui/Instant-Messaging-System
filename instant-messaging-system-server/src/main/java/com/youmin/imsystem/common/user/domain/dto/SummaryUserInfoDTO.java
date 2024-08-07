package com.youmin.imsystem.common.user.domain.dto;


import com.fasterxml.jackson.annotation.JsonInclude;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@NoArgsConstructor
@AllArgsConstructor
@Builder
@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class SummaryUserInfoDTO {

    @ApiModelProperty("user id")
    private Long uid;

    @ApiModelProperty("user name")
    private String name;

    @ApiModelProperty("user avatar url")
    private String avatar;

    @ApiModelProperty("user wearing badge item id")
    private Long wearingItemId;

    @ApiModelProperty("whether this user requiring refresh")
    private Boolean needRefresh = Boolean.TRUE;

    @ApiModelProperty("User acquire item")
    private List<Long> itemIds;

    @ApiModelProperty("user location")
    private String locPlace;

    public static SummaryUserInfoDTO skip(Long uid){
        SummaryUserInfoDTO dto = new SummaryUserInfoDTO();
        dto.setUid(uid);
        dto.setNeedRefresh(Boolean.FALSE);
        return dto;
    }



}
