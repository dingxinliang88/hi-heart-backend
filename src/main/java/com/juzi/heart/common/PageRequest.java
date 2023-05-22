package com.juzi.heart.common;

import lombok.Data;

import java.io.Serializable;

/**
 * 分页请求体
 *
 * @author codejuzi
 */
@Data
public class PageRequest implements Serializable {


    private static final long serialVersionUID = 1058094054659773745L;

    /**
     * 当前页数
     */
    private Integer pageNum;

    /**
     * 每页数据数
     */
    private Integer pageSize;
}
