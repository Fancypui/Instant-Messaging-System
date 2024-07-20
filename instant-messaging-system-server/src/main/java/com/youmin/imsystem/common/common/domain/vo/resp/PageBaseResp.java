package com.youmin.imsystem.common.common.domain.vo.resp;

import com.baomidou.mybatisplus.core.metadata.IPage;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@ApiModel("Base Pagination Response Body")
public class PageBaseResp<T> {

    @ApiModelProperty("Current Page")
    private Integer pageNo;

    @ApiModelProperty("The page size of every page")
    private Integer pageSize;

    @ApiModelProperty("total count")
    private Long totalRecords;

    @ApiModelProperty("whether is the last page")
    private Boolean isLast = Boolean.FALSE;

    @ApiModelProperty("Actual Data")
    private List<T> list;


    public static <T> PageBaseResp<T> empty() {
        PageBaseResp<T> r = new PageBaseResp<>();
        r.setPageNo(1);
        r.setPageSize(0);
        r.setIsLast(true);
        r.setTotalRecords(0L);
        r.setList(new ArrayList<>());
        return r;
    }

    public static <T> PageBaseResp<T> init(Integer pageNo, Integer pageSize, Long totalRecords, Boolean isLast, List<T> list) {
        return new PageBaseResp<>(pageNo, pageSize, totalRecords, isLast, list);
    }

    public static <T> PageBaseResp<T> init(Integer pageNo, Integer pageSize, Long totalRecords, List<T> list) {
        return new PageBaseResp<>(pageNo, pageSize, totalRecords, isLastPage(totalRecords, pageNo, pageSize), list);
    }

    public static <T> PageBaseResp<T> init(IPage<T> page) {
        return init((int) page.getCurrent(), (int) page.getSize(), page.getTotal(), page.getRecords());
    }

    public static <T> PageBaseResp<T> init(IPage page, List<T> list) {
        return init((int) page.getCurrent(), (int) page.getSize(), page.getTotal(), list);
    }

    public static <T> PageBaseResp<T> init(PageBaseResp resp, List<T> list) {
        return init(resp.getPageNo(), resp.getPageSize(), resp.getTotalRecords(), resp.getIsLast(), list);
    }


    public static Boolean isLastPage(long totalRecords, int pageNo, int pageSize) {
        if (pageSize == 0) {
            return false;
        }
        long pageTotal = totalRecords / pageSize + (totalRecords % pageSize == 0 ? 0 : 1);
        return pageNo >= pageTotal ? true : false;
    }
}
