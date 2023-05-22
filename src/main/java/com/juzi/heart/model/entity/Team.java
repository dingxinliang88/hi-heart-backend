package com.juzi.heart.model.entity;

import com.baomidou.mybatisplus.annotation.*;

import java.io.Serializable;
import java.util.Date;

import lombok.Data;

/**
 * 队伍表
 *
 * @TableName team
 */
@TableName(value = "team")
@Data
public class Team implements Serializable {
    /**
     * 主键、自增、非空
     */
    @TableId(type = IdType.AUTO)
    private Long id;

    /**
     * 队伍名称、非空
     */
    private String teamName;

    /**
     * 队伍描述，可以为空
     */
    private String description;

    /**
     * 队伍最大人数，非空，默认为5
     */
    private Integer maxNum;

    /**
     * 创建队伍人id，非空，普通索引
     */
    private Long createUserId;

    /**
     * 队长id，非空，普通索引
     */
    private Long leaderId;

    /**
     * 队伍状态，0 - 公开、1 - 私有，2 - 加密，默认为0
     */
    private Integer status;

    /**
     * 队伍密码，只有在队伍状态为加密状态下才有
     */
    private String teamPassword;

    /**
     * 队伍封面，可以为空，代码层面给默认值
     */
    private String teamAvatar;

    /**
     * 创建时间，默认为当前时间
     */
    private Date createTime;

    /**
     * 修改时间，默认为当前时间
     */
    private Date updateTime;

    /**
     * 逻辑删除标志，0 - 未删除、1 - 删除
     */
    @TableLogic
    private Integer isDelete;

    @TableField(exist = false)
    private static final long serialVersionUID = 1L;
}