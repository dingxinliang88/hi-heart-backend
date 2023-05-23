package com.juzi.heart.mapper;

import com.juzi.heart.model.entity.Team;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import javax.annotation.Resource;

import java.util.List;


/**
 * @author codejuzi
 */
@SpringBootTest
class TeamMapperTest {

    @Resource
    private TeamMapper teamMapper;

    @Test
    void listJoinTeam() {
        Long userId = 4L;
        List<Team> teams = teamMapper.listJoinTeam(userId, Boolean.TRUE, Boolean.TRUE);
        System.out.println("teams = " + teams);
    }
}