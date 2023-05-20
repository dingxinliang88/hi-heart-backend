package com.juzi.heart.model.entity;

import com.baomidou.mybatisplus.annotation.*;

import java.io.Serializable;
import java.util.Date;

import lombok.Data;

/**
 * 标签表
 *
 * @TableName tag
 */
@TableName(value = "tag")
@Data
public class Tag implements Serializable {
    /**
     * 主键，自增
     */
    @TableId(type = IdType.AUTO)
    private Long id;

    /**
     * 标签名称（非空，唯一，唯一索引）
     */
    private String tagName;

    /**
     * 上传标签的用户（非空，普通索引）
     */
    private Long userId;

    /**
     * 标识是否为父标签（0 - 不是，1 - 是），默认值为0
     */
    private Integer hasChildren;

    /**
     * 父标签id（分类用，自身是父标签值为0，非空）
     */
    private Long parentId;

    /**
     * 创建时间，默认为当前时间
     */
    private Date createTime;

    /**
     * 修改时间，默认为当前时间
     */
    private Date updateTime;

    /**
     * 逻辑删除（0-未删除，1-删除），默认为0
     */
    @TableLogic
    private Integer isDelete;

    @TableField(exist = false)
    private static final long serialVersionUID = 1L;
}