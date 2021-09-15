package com.provider.controller;

import com.api.UserService;
import com.dto.UserDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/user")
public class TestControler {
    @Autowired
    private UserService userService ;

    @RequestMapping("/getUser")
    private UserDto getUser(){
        UserDto user = userService.getUser("1");
        return user;
    }

}
