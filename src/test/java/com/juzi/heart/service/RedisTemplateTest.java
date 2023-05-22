package com.juzi.heart.service;

import com.juzi.heart.model.entity.User;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;

import javax.annotation.Resource;

/**
 * @author codejuzi
 */
@SpringBootTest
public class RedisTemplateTest {

    @Resource
    private RedisTemplate<String, Object> redisTemplate;

    @Test
    void testRedis() {
        ValueOperations<String, Object> valueOperations = redisTemplate.opsForValue();

        // set
        valueOperations.set("name", "codejuzi");
        valueOperations.set("age", 1);
        valueOperations.set("score", 80.0);
        User user = new User();
        user.setUserName("codejuzi");
        user.setId(1L);
        valueOperations.set("user", user);

        // get
        String name = (String) valueOperations.get("name");
        System.out.println("name = " + name);
        Integer age = (Integer) valueOperations.get("age");
        System.out.println("age = " + age);
        Double score = (Double) valueOperations.get("score");
        System.out.println("score = " + score);
        User u = (User) valueOperations.get("user");
        System.out.println("u = " + u);
    }

}
