package com.juzi.heart.mapper;

import com.juzi.heart.model.entity.UserTeam;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;

/**
 * @author codejuzi
 * @description 针对表【user_team(用户队伍表)】的数据库操作Mapper
 * @createDate 2023-05-22 16:16:25
 * @Entity com.juzi.heart.model.entity.UserTeam
 */
@Mapper
public interface UserTeamMapper extends BaseMapper<UserTeam> {

    /**
     * 判断当前用户是否加入队伍
     *
     * @param teamId team id
     * @param userId user id
     * @return true - 已加入
     */
    Boolean userHasJoinTeam(Long teamId, Long userId);

    /**
     * 得到当前加入队伍的人数
     *
     * @param teamId team id
     * @return 加入队伍的人数
     */
    Integer hasJoinTeamNum(Long teamId);

    /**
     * 获取用户已经加入的队伍数量
     *
     * @param userId user id
     * @return 用户加入的队伍数量
     */
    Integer userHasJoinTeamNum(Long userId);

    /**
     * 用户退出队伍，删除用户队伍关系
     *
     * @param teamId team id
     * @param userId user id
     * @return true - 删除成功
     */
    Boolean userQuitTeam(Long teamId, Long userId);
}




