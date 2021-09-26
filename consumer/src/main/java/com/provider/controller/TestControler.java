package com.provider.controller;

import com.api.UserService;
import com.common.annotation.RpcReference;
import com.common.entity.ServerNode;
import com.dto.UserDto;
import com.provider.holder.RemoteServerHolder;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.concurrent.CopyOnWriteArrayList;

@RestController
@RequestMapping("/user")
public class TestControler {

    @RpcReference
    private UserService userService ;

    @RequestMapping("/getUser")
    private UserDto getUser(){
        UserDto user = userService.getUser("1");
        return user;
    }
    @RequestMapping("/getServerList")
    private Object getServerList(){
        CopyOnWriteArrayList<ServerNode> serverList = RemoteServerHolder.getServerList();
        return serverList;
    }
}
