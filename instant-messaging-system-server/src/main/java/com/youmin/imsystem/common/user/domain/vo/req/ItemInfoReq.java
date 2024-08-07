package com.youmin.imsystem.common.user.domain.vo.req;


import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.util.List;

@NoArgsConstructor
@AllArgsConstructor
@Builder
@Data
public class ItemInfoReq {

    @Size(max = 50)
    @ApiModelProperty("List of items that wanted their info to be loaded")
    private List<InfoReq> itemInfoList;


    @Data
    public static class InfoReq{
        @ApiModelProperty("item info id")
        private Long itemId;
        @ApiModelProperty("item's latest update time")
        private Long lastModifyTime;
    }
}
