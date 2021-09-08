package com.provider.controller;

import com.provider.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/user")
public class TestControler {
    @Autowired
    private UserService userService ;
    @RequestMapping("/getUser")
    private void getUser(){
        userService.getUser("1","1");
    }
}
