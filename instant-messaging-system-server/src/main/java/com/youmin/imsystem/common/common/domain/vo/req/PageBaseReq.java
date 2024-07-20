package com.youmin.imsystem.common.common.domain.vo.req;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.Max;
import javax.validation.constraints.Min;

@Data
@ApiModel("Basic pagination request")
public class PageBaseReq {

    @ApiModelProperty("Page size")
    @Min(0)
    @Max(50)
    private Integer pageSize = 10;

    @ApiModelProperty("Page Index")
    private Integer pageNo = 1;

    public Page plugPage(){
        return new Page(pageNo, pageSize);
    }
}
