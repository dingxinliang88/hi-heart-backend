package com.juzi.heart.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.juzi.heart.common.PageRequest;
import com.juzi.heart.common.SingleIdRequest;
import com.juzi.heart.model.dto.team.*;
import com.juzi.heart.model.entity.Team;
import com.juzi.heart.model.vo.Team.TeamUserVO;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

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

    /**
     * 修改队伍信息
     *
     * @param teamUpdateRequest 修改队伍封装信息
     * @param request           http request
     * @return true - 修改成功
     */
    Boolean updateTeam(TeamUpdateRequest teamUpdateRequest, HttpServletRequest request);

    /**
     * 用户加入队伍
     *
     * @param teamJoinRequest 加入队伍封装信息
     * @param request         http request
     * @return true - 加入成功
     */
    Boolean joinTeam(TeamJoinRequest teamJoinRequest, HttpServletRequest request);

    /**
     * 用户退出队伍
     *
     * @param teamQuitRequest 退出队伍封装信息
     * @param request         http request
     * @return true - 退出成功
     */
    Boolean quitTeam(TeamQuitRequest teamQuitRequest, HttpServletRequest request);

    /**
     * 解散队伍
     *
     * @param singleIdRequest 删除请求信息
     * @param request         http request
     * @return true - 删除成功
     */
    Boolean deleteTeam(SingleIdRequest singleIdRequest, HttpServletRequest request);

    /**
     * 队长转让队伍
     *
     * @param teamTransferRequest 转让队伍请求
     * @param request             http request
     * @return true - 删除成功
     */
    Boolean transferTeam(TeamTransferRequest teamTransferRequest, HttpServletRequest request);

    /**
     * 分页获取所有公开和加密的队伍
     *
     * @param pageRequest 分页信息
     * @param request     http request
     * @return team user vo page
     */
    Page<TeamUserVO> listTeam(PageRequest pageRequest, HttpServletRequest request);

    /**
     * 展示我的队伍
     *
     * @param type        类型：0 - 加入（包含我是队长的）， 1 - 我是队长的， 2 - 我创建的队伍
     * @param pageRequest 分页请求信息
     * @param request     http request
     * @return team user vo list
     */
    Page<TeamUserVO> listMyTeam(Integer type, PageRequest pageRequest, HttpServletRequest request);
}
