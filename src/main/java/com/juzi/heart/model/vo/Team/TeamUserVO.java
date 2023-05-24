package com.juzi.heart.model.vo.Team;

import com.juzi.heart.model.entity.Team;
import com.juzi.heart.model.vo.user.UserVO;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
 * 队伍用户封装信息
 *
 * @author codejuzi
 */
@Data
public class TeamUserVO implements Serializable {

    private static final long serialVersionUID = -1018783522134751262L;

    public TeamUserVO(Team team) {
        this.teamId = team.getId();
        this.description = team.getDescription();
        this.teamName = team.getTeamName();
        this.createUserId = team.getCreateUserId();
        this.leaderId = team.getLeaderId();
        this.maxNum = team.getMaxNum();
        this.status = team.getStatus();
        this.teamAvatar = team.getTeamAvatar();
        this.createTime = team.getCreateTime();
    }

    /**
     * 队伍id
     */
    private Long teamId;

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
     * 队伍封面，可以为空，代码层面给默认值
     */
    private String teamAvatar;

    /**
     * 创建时间，默认为当前时间
     */
    private Date createTime;

    /**
     * 当前已经加入队伍的人数
     */
    private Integer joinNum;

    /**
     * 当前用户是否加入，如果未登录，默认未false
     */
    private Boolean hasJoin;

    /**
     * 队伍队长
     */
    private UserVO leader;

}
