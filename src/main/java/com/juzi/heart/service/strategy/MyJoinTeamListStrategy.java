package com.juzi.heart.service.strategy;

import com.juzi.heart.mapper.TeamMapper;
import com.juzi.heart.model.entity.Team;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.List;

/**
 * 展示我加入的队伍（包含我是队长的）
 *
 * @author codejuzi
 */
@Component
public class MyJoinTeamListStrategy implements TeamListStrategy {
    @Resource
    private TeamMapper teamMapper;

    @Override
    public List<Team> listTeam(Long userId) {
        return teamMapper.listMyTeam(userId, Boolean.FALSE, Boolean.FALSE);
    }
}
