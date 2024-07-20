package com.youmin.imsystem.common.common.domain.vo.req;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.fasterxml.jackson.annotation.JsonIgnore;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.apache.commons.lang3.StringUtils;

import javax.validation.constraints.Max;
import javax.validation.constraints.Min;

@Data
@ApiModel("Cursor pagination request")
@NoArgsConstructor
@AllArgsConstructor
public class CursorBaseReq {

    @Min(0)
    @Max(100)
    @ApiModelProperty("page size")
    private Integer pageSize = 10;

    @ApiModelProperty("Cursor")
    private String cursor;

    public Page plusPage(){
        return new Page(1,this.pageSize,false);
    }

    @JsonIgnore
    public Boolean isFirstPage(){
        return StringUtils.isEmpty(cursor);
    }
}
