package com.youmin.imsystem.common.user.domain.vo.req;

import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.Size;
import java.util.List;

@NoArgsConstructor
@AllArgsConstructor
@Data
@Builder
public class FriendCheckReq {


    @NotEmpty
    @Size(max =50)
    @ApiModelProperty("Verify friend uid")
    private List<Long> uidList;
}
