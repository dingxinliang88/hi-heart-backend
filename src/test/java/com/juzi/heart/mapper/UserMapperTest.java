package com.juzi.heart.mapper;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import javax.annotation.Resource;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * @author codejuzi
 */
@SpringBootTest
class UserMapperTest {

    @Resource
    private UserMapper userMapper;
    @Test
    void listLeaderOrCreatorId() {
        String searchText = "sw";
        List<Long> idList = userMapper.listLeaderOrCreatorId(searchText);
        System.out.println("idList = " + idList);
    }
}