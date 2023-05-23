package com.juzi.heart.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.juzi.heart.common.BaseResponse;
import com.juzi.heart.common.StatusCode;
import com.juzi.heart.model.dto.team.TeamAddRequest;
import com.juzi.heart.model.dto.team.TeamQueryRequest;
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
}