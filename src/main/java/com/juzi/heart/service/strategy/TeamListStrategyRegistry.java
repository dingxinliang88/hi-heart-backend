package com.juzi.heart.service.strategy;

import com.juzi.heart.common.StatusCode;
import com.juzi.heart.utils.ThrowUtils;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

import static com.juzi.heart.constant.TeamConstants.*;

/**
 * 展示队伍类型策略注册器
 *
 * @author codejuzi
 */
@Component
public class TeamListStrategyRegistry {

    @Resource
    private MyJoinTeamListStrategy myJoinTeamListStrategy;

    @Resource
    private MyLeadTeamListStrategy myLeadTeamListStrategy;

    @Resource
    private MyCreateTeamListStrategy myCreateTeamListStrategy;

    @SuppressWarnings("ALL")
    private static Map<Integer, TeamListStrategy> teamListStrategyMap;

    @PostConstruct
    private void doInit() {
        teamListStrategyMap = new HashMap<>() {{
            put(MY_JOIN, myJoinTeamListStrategy);
            put(MY_LEAD, myLeadTeamListStrategy);
            put(MY_CREATE, myCreateTeamListStrategy);
        }};
    }

    public TeamListStrategy getTeamListStrategyByType(Integer type) {
        ThrowUtils.throwIf(Objects.isNull(teamListStrategyMap), StatusCode.SYSTEM_ERROR, "注册器初始化异常");
        ThrowUtils.throwIf(Objects.isNull(type), StatusCode.PARAMS_ERROR, "类别为空！");
        return teamListStrategyMap.get(type);
    }
}
