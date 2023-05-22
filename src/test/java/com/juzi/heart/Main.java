package com.juzi.heart;

import org.apache.commons.lang3.RandomUtils;
import org.springframework.util.DigestUtils;

import java.nio.charset.StandardCharsets;

import static com.juzi.heart.constant.UserConstants.SALT;

/**
 * @author codejuzi
 */
public class Main {
    public static void main(String[] args) {
//        String admin = "88888888";
//        System.out.println(DigestUtils.md5DigestAsHex((SALT + admin).getBytes(StandardCharsets.UTF_8)));
        for (int i = 0; i < 10; i++) {
            System.out.println(RandomUtils.nextInt(0, 2));
        }
    }
}
