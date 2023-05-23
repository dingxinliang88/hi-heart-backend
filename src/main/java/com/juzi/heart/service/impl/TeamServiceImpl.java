package com.juzi.heart.service.impl;

import java.util.Date;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.juzi.heart.common.StatusCode;
import com.juzi.heart.exception.BusinessException;
import com.juzi.heart.manager.UserManager;
import com.juzi.heart.mapper.TeamMapper;
import com.juzi.heart.mapper.UserTeamMapper;
import com.juzi.heart.model.dto.team.TeamAddRequest;
import com.juzi.heart.model.dto.team.TeamQueryRequest;
import com.juzi.heart.model.entity.Team;
import com.juzi.heart.model.entity.User;
import com.juzi.heart.model.entity.UserTeam;
import com.juzi.heart.model.vo.Team.TeamUserVO;
import com.juzi.heart.model.vo.user.UserVO;
import com.juzi.heart.service.TeamService;
import com.juzi.heart.service.UserService;
import com.juzi.heart.service.UserTeamService;
import com.juzi.heart.utils.ThrowUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import static com.juzi.heart.constant.BusinessConstants.DEFAULT_PAGE_NUM;
import static com.juzi.heart.constant.BusinessConstants.DEFAULT_PAGE_SIZE;
import static com.juzi.heart.constant.TeamConstants.*;
import static com.juzi.heart.constant.UserConstants.ADMIN;
import static com.juzi.heart.model.enums.TeamStatusEnums.TEAM_STATUS_LIST;

/**
 * @author codejuzi
 * @description 针对表【team(队伍表)】的数据库操作Service实现
 * @createDate 2023-05-22 16:16:12
 */
@Service
public class TeamServiceImpl extends ServiceImpl<TeamMapper, Team>
        implements TeamService {

    @Resource
    private UserTeamService userTeamService;

    @Resource
    private UserService userService;

    @Resource
    private UserManager userManager;

    @Resource
    private TeamMapper teamMapper;

    @Resource
    private UserTeamMapper userTeamMapper;

    @Transactional(rollbackFor = {BusinessException.class})
    @Override
    public Long createTeam(TeamAddRequest teamAddRequest, HttpServletRequest request) {
        // 校验
        ThrowUtils.throwIf(Objects.isNull(teamAddRequest), StatusCode.PARAMS_ERROR, "创建队伍参数不能为空！");
        String teamName = teamAddRequest.getTeamName();
        String description = teamAddRequest.getDescription();
        Integer maxNum = teamAddRequest.getMaxNum();
        Integer status = teamAddRequest.getStatus();
        String teamPassword = teamAddRequest.getTeamPassword();
        String teamAvatar = teamAddRequest.getTeamAvatar();
        // 队伍标题不能为空 && 队伍标题长度 <= 20
        ThrowUtils.throwIf(StringUtils.isBlank(teamName), StatusCode.PARAMS_ERROR, "队伍名称不能为空！"
        );
        ThrowUtils.throwIf(StringUtils.isNotBlank(teamName) && teamName.length() > TEAM_NAME_MAX_LEN,
                StatusCode.PARAMS_ERROR, "队伍名称长度不能超过20");
        // 队伍描述长度<= 512
        ThrowUtils.throwIf(StringUtils.isNotBlank(description) && description.length() > TEAM_DESC_MAX_LEN,
                StatusCode.PARAMS_ERROR, "队伍描述过长！");
        // 队伍最大人数 1 <  maxNum  <= 20
        ThrowUtils.throwIf(maxNum <= TEAM_MAX_NUM_BEGIN || maxNum >= TEAM_MAX_NUM_END,
                StatusCode.PARAMS_ERROR, "队伍最大人数在2到20之间！");
        // status不能为空，默认为公开（0），如果status为加密状态（2），则一定要有密码，且密码长度 <= 32
        ThrowUtils.throwIf(ENCRYPTED.equals(status) && StringUtils.isBlank(teamPassword),
                StatusCode.PARAMS_ERROR, "加密队伍密码不能为空");
        ThrowUtils.throwIf(StringUtils.isNotBlank(teamAvatar) && teamPassword.length() > TEAM_PWD_MAX_LEN,
                StatusCode.PARAMS_ERROR, "密码长度不能大于32");
        // 一个用户最多创建5个队伍
        UserVO loginUser = userManager.getLoginUser(request);
        Long loginUserId = loginUser.getId();
        int createdTeamNum = teamMapper.getTeamNumByUserId(loginUserId);
        ThrowUtils.throwIf(!ADMIN.equals(loginUser.getUserRole()) && createdTeamNum >= USER_CREATE_TEAM_MAX_NUM,
                StatusCode.NO_AUTH_ERROR, "您创建的队伍数已达上限5个");

        // 插入队伍信息到队伍表
        Team team = new Team();
        team.setTeamName(teamName);
        description = StringUtils.isBlank(description) ? DEFAULT_TEAM_DESC : description;
        team.setDescription(description);
        team.setMaxNum(maxNum);
        team.setCreateUserId(loginUserId);
        team.setLeaderId(loginUserId);
        team.setStatus(status);
        team.setTeamPassword(teamPassword);
        teamAvatar = StringUtils.isBlank(teamAvatar) ? DEFAULT_TEAM_AVATAR : teamAvatar;
        team.setTeamAvatar(teamAvatar);
        this.save(team);

        Long teamId = team.getId();
        // 插入用户队伍关系到用户队伍关系表
        UserTeam userTeam = new UserTeam();
        userTeam.setUserId(loginUserId);
        userTeam.setTeamId(teamId);
        userTeam.setJoinTime(new Date());
        userTeamService.save(userTeam);

        return teamId;
    }

    @Override
    public Page<TeamUserVO> queryTeam(TeamQueryRequest teamQueryRequest, HttpServletRequest request) {
        ThrowUtils.throwIf(Objects.isNull(teamQueryRequest), StatusCode.PARAMS_ERROR, "查询参数不能为空！");
        Integer pageNum = teamQueryRequest.getPageNum();
        Integer pageSize = teamQueryRequest.getPageSize();
        LambdaQueryWrapper<Team> queryWrapper = this.getQueryWrapper(teamQueryRequest);
        pageSize = Objects.isNull(pageSize) || pageSize <= 0 ? DEFAULT_PAGE_SIZE : pageSize;
        pageNum = Objects.isNull(pageNum) || pageNum <= 0 ? DEFAULT_PAGE_NUM : pageNum;
        Page<Team> teamPage = this.page(new Page<>(pageNum, pageSize), queryWrapper);

        // 获取当前用户，允许为空
        UserVO loginUser = userManager.getLoginUserPermitNull(request);
        List<Team> teamList = teamPage.getRecords();
        List<TeamUserVO> teamUserVOList = teamList.stream().map(team -> this.getTeamUserVO(team, loginUser))
                .collect(Collectors.toList());
        // 封装返回结果
        Page<TeamUserVO> teamUserVOPage = new Page<>(
                teamPage.getCurrent(), teamPage.getSize(), teamPage.getTotal()
        );
        teamUserVOPage.setRecords(teamUserVOList);
        return teamUserVOPage;
    }

    /**
     * 得到查询队伍的查询wrapper
     *
     * @param teamQueryRequest 队伍查询请求信息
     * @return query wrapper
     */
    private LambdaQueryWrapper<Team> getQueryWrapper(TeamQueryRequest teamQueryRequest) {
        String searchText = teamQueryRequest.getSearchText();
        Integer maxNum = teamQueryRequest.getMaxNum();
        Long createUserId = teamQueryRequest.getCreateUserId();
        Long leaderId = teamQueryRequest.getLeaderId();
        Integer status = teamQueryRequest.getStatus();

        // 封装查询条件
        LambdaQueryWrapper<Team> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.like(StringUtils.isNotBlank(searchText), Team::getTeamName, searchText).or()
                .like(StringUtils.isNotBlank(searchText), Team::getDescription, searchText).or()
                .eq(!Objects.isNull(maxNum) && (maxNum <= TEAM_MAX_NUM_BEGIN || maxNum >= TEAM_MAX_NUM_END), Team::getMaxNum, maxNum).or()
                .eq(!Objects.isNull(createUserId) && createUserId > 0, Team::getCreateUserId, createUserId).or()
                .eq(!Objects.isNull(leaderId) && leaderId > 0, Team::getLeaderId, leaderId).or()
                .eq(!Objects.isNull(status) && TEAM_STATUS_LIST.contains(status) && !ENCRYPTED.equals(status), Team::getStatus, status);
        return queryWrapper;
    }

    /**
     * 封装TeamUserVO信息
     *
     * @param team      team info
     * @param loginUser login user, may be null
     * @return team user vo
     */
    private TeamUserVO getTeamUserVO(Team team, UserVO loginUser) {
        Long teamId = team.getId();
        TeamUserVO teamUserVO = new TeamUserVO(team);
        // 判断当前用户是否加入队伍
        // select count(1) from user_team where isDelete = 0 and (teamId = #{team.getId()} and userId = #{loginUser.getId()});
        if (!Objects.isNull(loginUser)) {
            Boolean hasJoinTeam = userTeamMapper.userHasJoinTeam(teamId, loginUser.getId());
            teamUserVO.setHasJoin(hasJoinTeam);
        }
        Long teamLeaderId = team.getLeaderId();
        User leaderUser = userService.getById(teamLeaderId);
        UserVO leader = userService.getUserVO(leaderUser);
        teamUserVO.setLeader(leader);
        // 获取当前队伍加入的人数
        // select count(1) from user_team where isDelete = 0 and teamId = #{team.getId()};
        Integer hasJoinTeamNum = userTeamMapper.hasJoinTeamNum(teamId);
        teamUserVO.setJoinNum(hasJoinTeamNum);
        return teamUserVO;
    }
}




