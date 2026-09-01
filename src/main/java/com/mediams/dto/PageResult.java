package com.mediams.dto;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import lombok.Builder;
import java.util.List;

/**
 * 分页结果类
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PageResult<T> {

    /** 当前页码 */
    private Long pageNum;

    /** 每页数量 */
    private Long pageSize;

    /** 总记录数 */
    private Long total;

    /** 总页数 */
    private Long pages;

    /** 数据列表 */
    private List<T> list;

    public static <T> PageResult<T> of(List<T> list, Long total, Long pageNum, Long pageSize) {
        Long pages = total % pageSize == 0 ? total / pageSize : total / pageSize + 1;
        return PageResult.<T>builder()
                .list(list)
                .total(total)
                .pageNum(pageNum)
                .pageSize(pageSize)
                .pages(pages)
                .build();
    }
}