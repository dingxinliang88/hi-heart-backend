package com.juzi.heart.service;

import org.junit.jupiter.api.Test;
import org.redisson.api.RList;
import org.redisson.api.RedissonClient;
import org.springframework.boot.test.context.SpringBootTest;

import javax.annotation.Resource;

/**
 * @author codejuzi
 */
@SpringBootTest
public class RedissonTest {

    @Resource
    private RedissonClient redissonClient;

    @Test
    void testRedisson() {
        RList<Object> rList = redissonClient.getList("test-list");
        rList.add("codejuzi");
        rList.add("codejuzi2");
        System.out.println("rList = " + rList);
        System.out.println(rList.get(0));
        rList.remove(0);
        rList.remove(1);
    }
}
