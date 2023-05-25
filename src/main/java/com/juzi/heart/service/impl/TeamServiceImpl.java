package com.juzi.heart.service.impl;

import java.util.ArrayList;
import java.util.Date;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.juzi.heart.common.PageRequest;
import com.juzi.heart.common.SingleIdRequest;
import com.juzi.heart.common.StatusCode;
import com.juzi.heart.exception.BusinessException;
import com.juzi.heart.manager.AuthManager;
import com.juzi.heart.manager.UserManager;
import com.juzi.heart.mapper.TeamMapper;
import com.juzi.heart.mapper.UserMapper;
import com.juzi.heart.mapper.UserTeamMapper;
import com.juzi.heart.model.dto.team.*;
import com.juzi.heart.model.entity.Team;
import com.juzi.heart.model.entity.User;
import com.juzi.heart.model.entity.UserTeam;
import com.juzi.heart.model.vo.Team.TeamUserVO;
import com.juzi.heart.model.vo.user.UserVO;
import com.juzi.heart.service.TeamService;
import com.juzi.heart.service.UserService;
import com.juzi.heart.service.UserTeamService;
import com.juzi.heart.service.strategy.TeamListStrategy;
import com.juzi.heart.service.strategy.TeamListStrategyRegistry;
import com.juzi.heart.utils.ThrowUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

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
    private TeamListStrategyRegistry teamListStrategyRegistry;

    @Resource
    private UserTeamService userTeamService;

    @Resource
    private UserService userService;

    @Resource
    private UserManager userManager;

    @Resource
    private AuthManager authManager;

    @Resource
    private TeamMapper teamMapper;

    @Resource
    private UserMapper userMapper;

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
        checkTeamInfoValid(teamName, description, maxNum, status, teamPassword);

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
        String teamAvatar = teamAddRequest.getTeamAvatar();
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
        return getTeamUserVOPage(teamPage, loginUser, teamList);
    }

    @Override
    public Boolean updateTeam(TeamUpdateRequest teamUpdateRequest, HttpServletRequest request) {
        ThrowUtils.throwIf(Objects.isNull(teamUpdateRequest), StatusCode.PARAMS_ERROR, "修改队伍参数不能为空");
        Long teamId = teamUpdateRequest.getTeamId();
        Team teamFromDb = this.getById(teamId);
        // 管理员 || 队长才可以修改
        authManager.adminOrMe(teamFromDb.getLeaderId(), request);

        // 判断是否需要更新
        if (!needUpdate(teamFromDb, teamUpdateRequest)) {
            // 不需要更新
            return Boolean.TRUE;
        }
        // 需要更新
        String teamName = teamUpdateRequest.getTeamName();
        String description = teamUpdateRequest.getDescription();
        Integer maxNum = teamUpdateRequest.getMaxNum();
        Integer status = teamUpdateRequest.getStatus();
        String teamPassword = teamUpdateRequest.getTeamPassword();
        String teamAvatar = teamUpdateRequest.getTeamAvatar();
        LambdaUpdateWrapper<Team> updateWrapper = new LambdaUpdateWrapper<>();
        updateWrapper.eq(Team::getId, teamId)
                .set(Team::getTeamName, teamName)
                .set(StringUtils.isNotBlank(description), Team::getDescription, description)
                .set(Team::getMaxNum, maxNum)
                .set(Team::getStatus, status)
                .set(StringUtils.isNotBlank(teamPassword), Team::getTeamPassword, teamPassword)
                .set(StringUtils.isNotBlank(teamAvatar), Team::getTeamAvatar, teamAvatar);
        return this.update(updateWrapper);
    }

    @Override
    public Boolean joinTeam(TeamJoinRequest teamJoinRequest, HttpServletRequest request) {
        ThrowUtils.throwIf(Objects.isNull(teamJoinRequest), StatusCode.PARAMS_ERROR, "加入队伍请求信息不能为空！");

        UserVO loginUser = userManager.getLoginUser(request);
        Long userId = loginUser.getId();
        Long teamId = teamJoinRequest.getTeamId();

        // 队伍必须存在，未满
        Team team = this.getById(teamId);
        ThrowUtils.throwIf(Objects.isNull(team), StatusCode.NOT_FOUND_ERROR, "待加入队伍不存在！");
        Integer hasJoinTeamNum = userTeamMapper.hasJoinTeamNum(teamId);
        ThrowUtils.throwIf(team.getMaxNum() < hasJoinTeamNum, StatusCode.NO_AUTH_ERROR, "该队伍已满员！");

        // 不能加入队长为自己的队伍
        Long teamLeaderId = team.getLeaderId();

        ThrowUtils.throwIf(userId.equals(teamLeaderId), StatusCode.OPERATION_ERROR, "不能加入自己的队伍");
        // 不能重复加入已经加入的队伍
        Boolean hasJoinTeam = userTeamMapper.userHasJoinTeam(teamId, userId);
        ThrowUtils.throwIf(hasJoinTeam, StatusCode.OPERATION_ERROR, "您已在队伍内！");

        // 用户最多加入20支队伍
        int userHasJoinTeamNum = userTeamMapper.userHasJoinTeamNum(userId);
        ThrowUtils.throwIf(userHasJoinTeamNum >= USER_JOIN_TEAM_MAX_NUM,
                StatusCode.NO_AUTH_ERROR, "您加入的队伍数量已达上限");

        // 禁止加入私有的队伍（只能邀请）
        Integer status = team.getStatus();
        ThrowUtils.throwIf(CONST_PRIVATE.equals(status), StatusCode.NO_AUTH_ERROR, "不能加入私有的队伍！");

        // 如果队伍是加密的，密码要匹配
        if (CONST_ENCRYPTED.equals(status)) {
            String teamPassword = teamJoinRequest.getTeamPassword();
            ThrowUtils.throwIf(StringUtils.isBlank(teamPassword), StatusCode.PARAMS_ERROR, "入队密码不能为空");
            ThrowUtils.throwIf(!team.getTeamPassword().equals(teamPassword), StatusCode.NO_AUTH_ERROR, "入队密码错误");
        }

        // 用户队伍信息入库
        UserTeam userTeam = new UserTeam();
        userTeam.setUserId(userId);
        userTeam.setTeamId(teamId);
        userTeam.setJoinTime(new Date());
        return userTeamService.save(userTeam);
    }

    @Transactional(rollbackFor = {BusinessException.class})
    @Override
    public Boolean quitTeam(TeamQuitRequest teamQuitRequest, HttpServletRequest request) {
        ThrowUtils.throwIf(Objects.isNull(teamQuitRequest), StatusCode.PARAMS_ERROR, "退出队伍请求信息不能为空！");

        // 判断队伍是否存在
        Long teamId = teamQuitRequest.getTeamId();
        Team team = this.getById(teamId);
        ThrowUtils.throwIf(Objects.isNull(team), StatusCode.NOT_FOUND_ERROR, "所要退出的队伍不存在");

        // 判断当前用户是否加入该队伍
        UserVO loginUser = userManager.getLoginUser(request);
        Long userId = loginUser.getId();
        Boolean userHasJoinTeam = userTeamMapper.userHasJoinTeam(teamId, userId);
        ThrowUtils.throwIf(Boolean.FALSE.equals(userHasJoinTeam), StatusCode.OPERATION_ERROR, "您尚未加入该队伍");

        // 根据队伍人数分情况
        int hasJoinTeamNum = userTeamMapper.hasJoinTeamNum(teamId);
        if (hasJoinTeamNum == 1) {
            // 队伍只剩下一人，直接解散
            this.removeById(teamId);
        } else {
            // 如果是队长退出队伍，将队伍转让给第二早加入队伍的用户
            if (team.getLeaderId().equals(userId)) {
                LambdaQueryWrapper<UserTeam> queryWrapper = new LambdaQueryWrapper<>();
                queryWrapper.eq(UserTeam::getTeamId, teamId)
                        .last("order by joinTime asc limit 2");
                List<UserTeam> userTeamList = userTeamService.list(queryWrapper);
                ThrowUtils.throwIf(userTeamList.size() < 2, StatusCode.SYSTEM_ERROR, "队伍信息获取失败");
                Long nextLeaderId = userTeamList.get(1).getUserId();
                Team updateTeam = new Team();
                updateTeam.setId(teamId);
                updateTeam.setLeaderId(nextLeaderId);
                ThrowUtils.throwIf(!this.updateById(updateTeam), StatusCode.SYSTEM_ERROR, "队伍信息更新失败");
            }
        }

        // 移除用户队伍关系
        return userTeamMapper.userQuitTeam(teamId, userId);
    }

    @Transactional(rollbackFor = {BusinessException.class})
    @Override
    public Boolean deleteTeam(SingleIdRequest singleIdRequest, HttpServletRequest request) {
        ThrowUtils.throwIf(Objects.isNull(singleIdRequest), StatusCode.PARAMS_ERROR, "删除队伍请求信息不能为空！");
        Long teamId = singleIdRequest.getId();
        Team team = this.getById(teamId);
        ThrowUtils.throwIf(Objects.isNull(team), StatusCode.NOT_FOUND_ERROR, "要删除的队伍不存在");
        // 管理员 || 队长
        authManager.adminOrMe(team.getLeaderId(), request);

        // 删除队伍消息
        this.removeById(teamId);

        // 删除队伍用户关系
        return userTeamMapper.deleteTeam(teamId);
    }

    @Override
    public Boolean transferTeam(TeamTransferRequest teamTransferRequest, HttpServletRequest request) {
        ThrowUtils.throwIf(Objects.isNull(teamTransferRequest), StatusCode.PARAMS_ERROR, "转让队伍请求信息不能为空！");

        Long teamId = teamTransferRequest.getTeamId();
        Team team = this.getById(teamId);
        UserVO loginUser = userManager.getLoginUser(request);
        Long userId = loginUser.getId();
        // 校验是否是队长
        ThrowUtils.throwIf(!team.getLeaderId().equals(userId), StatusCode.NO_AUTH_ERROR, "您不是该队队长，不能转让该队伍");

        // 判断要转让的用户是否在队伍中
        Long nextLeaderId = teamTransferRequest.getNextLeaderId();
        Boolean nextLeaderHasJoinTeam = userTeamMapper.userHasJoinTeam(teamId, nextLeaderId);
        ThrowUtils.throwIf(Boolean.FALSE.equals(nextLeaderHasJoinTeam), StatusCode.NOT_FOUND_ERROR, "要转让的对象未加入队伍");

        // 转让
        Team transferTeam = new Team();
        transferTeam.setId(teamId);
        transferTeam.setLeaderId(nextLeaderId);

        return this.updateById(transferTeam);
    }

    @Override
    public Page<TeamUserVO> listTeam(PageRequest pageRequest, HttpServletRequest request) {
        LambdaQueryWrapper<Team> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(Team::getStatus, CONST_PUBLIC).or()
                .eq(Team::getStatus, CONST_ENCRYPTED);
        List<Team> teamList = this.list(queryWrapper);

        Integer pageNum = pageRequest.getPageNum();
        Integer pageSize = pageRequest.getPageSize();
        pageSize = Objects.isNull(pageSize) || pageSize <= 0 ? DEFAULT_PAGE_SIZE : pageSize;
        pageNum = Objects.isNull(pageNum) || pageNum <= 0 ? DEFAULT_PAGE_NUM : pageNum;
        Page<Team> teamPage = new Page<>(pageNum, pageSize, teamList.size());
        return getTeamUserVOPage(teamPage, userManager.getLoginUserPermitNull(request), teamList);
    }

    @Override
    public Page<TeamUserVO> listMyTeam(Integer type, PageRequest pageRequest, HttpServletRequest request) {
        ThrowUtils.throwIf(Objects.isNull(pageRequest), StatusCode.PARAMS_ERROR, "获取我加入的队伍分页参数为空");

        Integer pageNum = pageRequest.getPageNum();
        Integer pageSize = pageRequest.getPageSize();
        pageSize = Objects.isNull(pageSize) || pageSize <= 0 ? DEFAULT_PAGE_SIZE : pageSize;
        pageNum = Objects.isNull(pageNum) || pageNum <= 0 ? DEFAULT_PAGE_NUM : pageNum;

        UserVO loginUser = userManager.getLoginUser(request);
        Long userId = loginUser.getId();

        TeamListStrategy teamListStrategy = teamListStrategyRegistry.getTeamListStrategyByType(type);
        List<Team> teamList = teamListStrategy.listTeam(userId);

        Page<Team> teamPage = new Page<>(pageNum, pageSize, teamList.size());
        return getTeamUserVOPage(teamPage, loginUser, teamList);
    }

    @Override
    public List<UserVO> getJoinTeamUser(Long teamId) {
        Team team = this.getById(teamId);
        ThrowUtils.throwIf(Objects.isNull(team), StatusCode.PARAMS_ERROR, "队伍不存在");

        List<Long> joinTeamUserIdList = userTeamMapper.getJoinTeamUserIdList(teamId);
        ThrowUtils.throwIf(CollectionUtils.isEmpty(joinTeamUserIdList), StatusCode.SYSTEM_ERROR, "队伍用户表信息出错");
        LambdaQueryWrapper<User> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.in(User::getId, joinTeamUserIdList);
        List<User> userList = userService.list(queryWrapper);
        return userList.stream().map(user -> userService.getUserVO(user)).collect(Collectors.toList());
    }


    /**
     * 得到查询队伍的查询wrapper
     *
     * @param teamQueryRequest 队伍查询请求信息
     * @return query wrapper
     */
    private LambdaQueryWrapper<Team> getQueryWrapper(TeamQueryRequest teamQueryRequest) {
        String searchText = teamQueryRequest.getSearchText();
        Integer status = teamQueryRequest.getStatus();

        List<Long> userIdList = new ArrayList<>();
        if (StringUtils.isNotBlank(searchText)) {
            userIdList = userMapper.listLeaderOrCreatorId(searchText);
        }
        // 封装查询条件
        LambdaQueryWrapper<Team> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.ne(Team::getStatus, CONST_PRIVATE)
                .like(StringUtils.isNotBlank(searchText), Team::getTeamName, searchText).or()
                .like(StringUtils.isNotBlank(searchText), Team::getDescription, searchText).or()
                .in(!CollectionUtils.isEmpty(userIdList), Team::getCreateUserId, userIdList).or()
                .in(!CollectionUtils.isEmpty(userIdList), Team::getLeaderId, userIdList).or()
                .eq(!Objects.isNull(status) && TEAM_STATUS_LIST.contains(status) && !CONST_PRIVATE.equals(status), Team::getStatus, status);
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

    /**
     * 判断更新请求是否需要更新
     *
     * @param teamFromDb        数据库中的team信息
     * @param teamUpdateRequest 更新队伍请求封装信息
     * @return true - 需要更新
     */
    private boolean needUpdate(Team teamFromDb, TeamUpdateRequest teamUpdateRequest) {
        String teamName = teamUpdateRequest.getTeamName();
        String description = teamUpdateRequest.getDescription();
        Integer maxNum = teamUpdateRequest.getMaxNum();
        Integer status = teamUpdateRequest.getStatus();
        String teamPassword = teamUpdateRequest.getTeamPassword();
        // 校验参数是否合法
        checkTeamInfoValid(teamName, description, maxNum, status, teamPassword);

        String teamAvatar = teamUpdateRequest.getTeamAvatar();

        boolean eTeamName = teamFromDb.getTeamName().equals(teamName);
        boolean eDesc = StringUtils.isNotBlank(description) && teamFromDb.getDescription().equals(description);
        boolean eMaxNum = teamFromDb.getMaxNum().equals(maxNum);
        boolean eStatus = teamFromDb.getStatus().equals(status);
        boolean ePwd = CONST_ENCRYPTED.equals(teamFromDb.getStatus()) && teamFromDb.getTeamPassword().equals(teamPassword);
        boolean eTeamAvatar = teamFromDb.getTeamAvatar().equals(teamAvatar);
        return !(eTeamName && eDesc && eStatus && eMaxNum && ePwd && eTeamAvatar);
    }

    /**
     * 校验队伍参数是否合法（新增，修改）
     *
     * @param teamName     队伍名称
     * @param description  队伍描述
     * @param maxNum       队伍最大人数
     * @param status       队伍状态
     * @param teamPassword 队伍密码
     */
    private void checkTeamInfoValid(String teamName, String description,
                                    Integer maxNum, Integer status, String teamPassword) {
        // 队伍标题不能为空 && 队伍标题长度 <= 20
        ThrowUtils.throwIf(StringUtils.isBlank(teamName), StatusCode.PARAMS_ERROR, "队伍名称不能为空！");
        ThrowUtils.throwIf(StringUtils.isNotBlank(teamName) && teamName.length() > TEAM_NAME_MAX_LEN,
                StatusCode.PARAMS_ERROR, "队伍名称长度不能超过20");
        // 队伍描述长度<= 512
        ThrowUtils.throwIf(StringUtils.isNotBlank(description) && description.length() > TEAM_DESC_MAX_LEN,
                StatusCode.PARAMS_ERROR, "队伍描述过长！");
        // 队伍最大人数 1 <  maxNum  <= 20
        ThrowUtils.throwIf(maxNum <= TEAM_MAX_NUM_BEGIN || maxNum >= TEAM_MAX_NUM_END,
                StatusCode.PARAMS_ERROR, "队伍最大人数在2到20之间！");
        // status不能为空，默认为公开（0），如果status为加密状态（2），则一定要有密码，且密码长度 <= 32
        ThrowUtils.throwIf(CONST_ENCRYPTED.equals(status) && StringUtils.isBlank(teamPassword),
                StatusCode.PARAMS_ERROR, "加密队伍密码不能为空");
        ThrowUtils.throwIf(StringUtils.isNotBlank(teamPassword) && teamPassword.length() > TEAM_PWD_MAX_LEN,
                StatusCode.PARAMS_ERROR, "密码长度不能大于32");
    }

    /**
     * 封装 team user vo page
     *
     * @param teamPage  team page
     * @param loginUser login user
     * @param teamList  team list
     * @return team user vo page info
     */
    private Page<TeamUserVO> getTeamUserVOPage(Page<Team> teamPage, UserVO loginUser, List<Team> teamList) {
        List<TeamUserVO> teamUserVOList = teamList.stream().map(team -> this.getTeamUserVO(team, loginUser))
                .collect(Collectors.toList());
        // 封装返回结果
        Page<TeamUserVO> teamUserVOPage = new Page<>(
                teamPage.getCurrent(), teamPage.getSize(), teamPage.getTotal()
        );
        teamUserVOPage.setRecords(teamUserVOList);
        return teamUserVOPage;
    }
}




