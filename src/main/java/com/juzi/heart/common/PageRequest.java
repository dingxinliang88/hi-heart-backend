package com.juzi.heart.common;

import lombok.Data;

/**
 * 分页请求体
 *
 * @author codejuzi
 */
@Data
public class PageRequest {

    private Integer pageNum;

    private Integer pageSize;
}
