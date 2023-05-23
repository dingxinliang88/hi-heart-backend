package com.juzi.heart.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.juzi.heart.common.BaseResponse;
import com.juzi.heart.common.PageRequest;
import com.juzi.heart.common.SingleIdRequest;
import com.juzi.heart.common.StatusCode;
import com.juzi.heart.model.dto.team.*;
import com.juzi.heart.model.vo.Team.TeamUserVO;
import com.juzi.heart.service.TeamService;
import com.juzi.heart.utils.ResultUtils;
import com.juzi.heart.utils.ThrowUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.Objects;

/**
 * @author codejuzi
 */
@Slf4j
@RestController
@RequestMapping("/team")
public class TeamController {

    @Resource
    private TeamService teamService;

    @PostMapping("/create")
    public BaseResponse<Long> createTeam(@RequestBody TeamAddRequest teamAddRequest, HttpServletRequest request) {
        ThrowUtils.throwIf(Objects.isNull(teamAddRequest), StatusCode.PARAMS_ERROR, "创建队伍参数不能为空！");
        Long teamId = teamService.createTeam(teamAddRequest, request);
        return ResultUtils.success(teamId);
    }

    @GetMapping("/query")
    public BaseResponse<Page<TeamUserVO>> queryTeam(TeamQueryRequest teamQueryRequest, HttpServletRequest request) {
        ThrowUtils.throwIf(Objects.isNull(teamQueryRequest), StatusCode.PARAMS_ERROR, "查询参数不能为空");
        Page<TeamUserVO> teamUserVOPage = teamService.queryTeam(teamQueryRequest, request);
        return ResultUtils.success(teamUserVOPage);
    }

    @PutMapping("/update")
    public BaseResponse<Boolean> updateTeam(@RequestBody TeamUpdateRequest teamUpdateRequest, HttpServletRequest request) {
        ThrowUtils.throwIf(Objects.isNull(teamUpdateRequest), StatusCode.PARAMS_ERROR, "修改参数不能为空");
        Boolean updateRes = teamService.updateTeam(teamUpdateRequest, request);
        return ResultUtils.success(updateRes, "修改成功");
    }

    @PostMapping("/join")
    public BaseResponse<Boolean> joinTeam(@RequestBody TeamJoinRequest teamJoinRequest, HttpServletRequest request) {
        ThrowUtils.throwIf(Objects.isNull(teamJoinRequest), StatusCode.PARAMS_ERROR, "加入队伍请求信息不能为空！");
        Boolean joinRes = teamService.joinTeam(teamJoinRequest, request);
        return ResultUtils.success(joinRes);
    }

    @PostMapping("/quit")
    public BaseResponse<Boolean> quitTeam(@RequestBody TeamQuitRequest teamQuitRequest, HttpServletRequest request) {
        ThrowUtils.throwIf(Objects.isNull(teamQuitRequest), StatusCode.PARAMS_ERROR, "退出队伍请求信息不能为空！");
        Boolean quitRes = teamService.quitTeam(teamQuitRequest, request);
        return ResultUtils.success(quitRes);
    }

    @DeleteMapping("/delete")
    public BaseResponse<Boolean> deleteTeam(@RequestBody SingleIdRequest singleIdRequest, HttpServletRequest request) {
        ThrowUtils.throwIf(Objects.isNull(singleIdRequest), StatusCode.PARAMS_ERROR, "解散队伍请求信息不能为空！");
        Boolean deleteRes = teamService.deleteTeam(singleIdRequest, request);
        return ResultUtils.success(deleteRes);
    }

    @PutMapping("/transfer")
    public BaseResponse<Boolean> transferTeam(@RequestBody TeamTransferRequest teamTransferRequest, HttpServletRequest request) {
        ThrowUtils.throwIf(Objects.isNull(teamTransferRequest), StatusCode.PARAMS_ERROR, "转让队伍请求信息不能为空！");
        Boolean transferRes = teamService.transferTeam(teamTransferRequest, request);
        return ResultUtils.success(transferRes);
    }

    @GetMapping("/my/join")
    public BaseResponse<Page<TeamUserVO>> listJoinTeam(PageRequest pageRequest, HttpServletRequest request) {
        Page<TeamUserVO> teamUserVOPage = teamService.listMyJoinTeam(pageRequest, request);
        return ResultUtils.success(teamUserVOPage);
    }

    @GetMapping("/my/lead")
    public BaseResponse<Page<TeamUserVO>> listLeadTeam(PageRequest pageRequest, HttpServletRequest request) {
        Page<TeamUserVO> teamUserVOPage = teamService.listMyLeadTeam(pageRequest, request);
        return ResultUtils.success(teamUserVOPage);
    }

    @GetMapping("/my/create")
    public BaseResponse<Page<TeamUserVO>> listCreateTeam(PageRequest pageRequest, HttpServletRequest request) {
        Page<TeamUserVO> teamUserVOPage = teamService.listMyCreateTeam(pageRequest, request);
        return ResultUtils.success(teamUserVOPage);
    }

}
