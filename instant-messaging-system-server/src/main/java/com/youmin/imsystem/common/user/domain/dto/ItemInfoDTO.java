package com.youmin.imsystem.common.user.domain.dto;

import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Builder
@Data
public class ItemInfoDTO {

    @ApiModelProperty("item id")
    private Long itemId;
    @ApiModelProperty("image url")
    private String img;
    @ApiModelProperty("item description")
    private String desc;
    @ApiModelProperty("item's latest update time")
    private Boolean needRefresh = Boolean.TRUE;

    public static ItemInfoDTO skip(Long itemId){
        ItemInfoDTO itemInfoDTO = new ItemInfoDTO();
        itemInfoDTO.setItemId(itemId);
        itemInfoDTO.setNeedRefresh(Boolean.FALSE);
        return itemInfoDTO;
    }
}
