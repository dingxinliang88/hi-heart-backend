package com.juzi.heart.model.dto.team;

import lombok.Data;

import java.io.Serializable;

/**
 * @author codejuzi
 */
@Data
public class TeamQuitRequest implements Serializable {

    private static final long serialVersionUID = 5704157347926154416L;

    /**
     * 队伍id
     */
    private Long teamId;
}
