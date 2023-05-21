package com.juzi.heart.common;

import lombok.Data;

import java.io.Serializable;

/**
 * 删除通用请求
 *
 * @author codejuzi
 */
@Data
public class DeleteRequest implements Serializable {

    private static final long serialVersionUID = 6566322826250035787L;

    /**
     * 待删除元素的id
     */
    private Long id;
}
