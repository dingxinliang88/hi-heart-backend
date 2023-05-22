package com.juzi.heart.model.dto.team;

import lombok.Data;

import java.io.Serializable;

/**
 * 新增队伍请求
 *
 * @author codejuzi
 */
@Data
public class TeamAddRequest implements Serializable {

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
}
