package com.juzi.heart.model.vo.tag;

import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * @author codejuzi
 */
@Data
public class TagVO implements Serializable {

    private static final long serialVersionUID = 6056363453515780280L;

    /**
     * 父标签id
     */
    private Long parentTagId;

    /**
     * 父标签名称
     */
    private String parentTagName;

    /**
     * 子标签名称列表
     */
    private List<String> childTagNameList;
}
