package com.juzi.heart.model.dto.team;

import lombok.Data;

import java.io.Serializable;

/**
 * @author codejuzi
 */
@Data
public class TeamJoinRequest implements Serializable {

    private static final long serialVersionUID = -8302918728606833957L;

    /**
     * 队伍Id
     */
    private Long teamId;

    /**
     * 队伍密码，只有在队伍状态为加密状态下才有
     */
    private String teamPassword;
}
