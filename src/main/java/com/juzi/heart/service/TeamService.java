package com.juzi.heart.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.juzi.heart.model.dto.team.TeamAddRequest;
import com.juzi.heart.model.dto.team.TeamQueryRequest;
import com.juzi.heart.model.entity.Team;
import com.baomidou.mybatisplus.extension.service.IService;
import com.juzi.heart.model.vo.Team.TeamUserVO;

import javax.servlet.http.HttpServletRequest;

/**
 * @author codejuzi
 * @description 针对表【team(队伍表)】的数据库操作Service
 * @createDate 2023-05-22 16:16:12
 */
public interface TeamService extends IService<Team> {

    /**
     * 创建队伍
     *
     * @param teamAddRequest 创建队伍信息封装
     * @param request        http request
     * @return 新创建的队伍id
     */
    Long createTeam(TeamAddRequest teamAddRequest, HttpServletRequest request);

    /**
     * 查询队伍
     *
     * @param teamQueryRequest 查询队伍封装信息
     * @param request          http request
     * @return 队伍分页信息
     */
    Page<TeamUserVO> queryTeam(TeamQueryRequest teamQueryRequest, HttpServletRequest request);

}
