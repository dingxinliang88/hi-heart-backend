package com.juzi.heart.model.dto.team;

import lombok.Data;

import java.io.Serializable;

/**
 * @author codejuzi
 */
@Data
public class TeamUpdateRequest implements Serializable {

    private static final long serialVersionUID = 6640885296914307488L;

    /**
     * 主键、自增、非空
     */
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
