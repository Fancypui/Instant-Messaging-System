package com.youmin.imsystem.common.user.domain.vo.resp;

import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Builder
@NoArgsConstructor
@AllArgsConstructor
@Data
public class FriendCheckResp {


    @ApiModelProperty("Final check result")
    private List<FriendCheck> checkedList;


    @Data
    public static class FriendCheck{

        private Long uid;
        private boolean isFriend;
    }
}
