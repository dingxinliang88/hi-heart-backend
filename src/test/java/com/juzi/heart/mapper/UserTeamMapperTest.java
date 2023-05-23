package com.juzi.heart.mapper;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import javax.annotation.Resource;

import static org.junit.jupiter.api.Assertions.*;

/**
 * @author codejuzi
 */
@SpringBootTest
class UserTeamMapperTest {

    @Resource
    private UserTeamMapper userTeamMapper;

    @Test
    void userHasJoinTeam() {
        Long userId = 4L;
        Long teamId = 1L;
        Boolean hasJoinTeam = userTeamMapper.userHasJoinTeam(teamId, userId);
        assertTrue(hasJoinTeam);
    }

    @Test
    void hasJoinTeamNum() {
        Long teamId = 1L;
        Integer hasJoinTeamNum = userTeamMapper.hasJoinTeamNum(teamId);
        System.out.println("hasJoinTeamNum = " + hasJoinTeamNum);
    }

}