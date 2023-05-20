package com.juzi.heart;

import org.springframework.util.DigestUtils;

import java.nio.charset.StandardCharsets;

import static com.juzi.heart.constant.UserConstants.SALT;

/**
 * @author codejuzi
 */
public class Main {
    public static void main(String[] args) {
        String admin = "admin123";
        System.out.println(DigestUtils.md5DigestAsHex((SALT + admin).getBytes(StandardCharsets.UTF_8)));
    }
}
