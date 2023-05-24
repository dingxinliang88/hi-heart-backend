package com.juzi.heart.service.strategy;

import com.juzi.heart.model.entity.Team;

import java.util.List;

/**
 * 获取队伍策略
 *
 * @author codejuzi
 */
public interface TeamListStrategy {
    /**
     * 展示我的队伍
     *
     * @param userId 用户id
     * @return team list
     */
    List<Team> listTeam(Long userId);
}
