package com.juzi.heart.mapper;

import com.juzi.heart.model.entity.Team;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

/**
 * @author codejuzi
 * @description 针对表【team(队伍表)】的数据库操作Mapper
 * @createDate 2023-05-22 16:16:12
 * @Entity com.juzi.heart.model.entity.Team
 */
@Mapper
public interface TeamMapper extends BaseMapper<Team> {

    /**
     * 得到 id为userId的用户创建的队伍数
     *
     * @param userId user id
     * @return num of team
     */
    int getTeamNumByUserId(Long userId);

    /**
     * 获取用户加入的队伍
     *
     * @param userId     user id
     * @param selfLead   是否只需要自己是队长的
     * @param selfCreate 是否只需要自己创建的，这个若为true，selfLead失效
     * @return team list
     */
    List<Team> listJoinTeam(Long userId, Boolean selfLead, Boolean selfCreate);
}




