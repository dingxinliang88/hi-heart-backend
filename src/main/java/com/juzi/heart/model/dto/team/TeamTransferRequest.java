package com.juzi.heart.model.dto.team;

import lombok.Data;

import java.io.Serializable;

/**
 * @author codejuzi
 */
@Data
public class TeamTransferRequest implements Serializable {

    private static final long serialVersionUID = 5179861701912895307L;

    private Long teamId;

    /**
     * 被转让用户id
     */
    private Long nextLeaderId;
}
