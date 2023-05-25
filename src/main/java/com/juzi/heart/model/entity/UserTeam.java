package com.juzi.heart.model.entity;

import com.baomidou.mybatisplus.annotation.*;

import java.io.Serializable;
import java.util.Date;

import lombok.Data;

/**
 * 用户队伍表
 *
 * @TableName user_team
 */
@TableName(value = "user_team")
@Data
public class UserTeam implements Serializable {
    /**
     * 主键，自增，非空
     */
    @TableId(type = IdType.AUTO)
    private Long id;

    /**
     * 用户id，非空
     */
    private Long userId;

    /**
     * 队伍id，非空
     */
    private Long teamId;

    /**
     * 加入时间，非空，默认为当前时间
     */
    private Date joinTime;

    /**
     * 创建时间，默认为当前时间
     */
    private Date createTime;

    @TableField(exist = false)
    private static final long serialVersionUID = 1L;
}