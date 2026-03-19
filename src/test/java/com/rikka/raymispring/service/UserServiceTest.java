package com.rikka.raymispring.service;

import com.rikka.raymispring.RaymiSpringApplication;
import com.rikka.raymispring.domain.User;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
@ActiveProfiles("test")
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT,classes = RaymiSpringApplication.class)
public class UserServiceTest {

    @Autowired
    private UserService userService;

    @Test
    void test1(){
        List<User> list = userService.list();
        System.out.println(list);
    }
}