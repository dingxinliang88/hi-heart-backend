package com.juzi.heart.model.dto.team;

import com.juzi.heart.common.PageRequest;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serializable;

/**
 * 新增队伍请求
 *
 * @author codejuzi
 */
@EqualsAndHashCode(callSuper = true)
@Data
public class TeamQueryRequest extends PageRequest implements Serializable {

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
}
