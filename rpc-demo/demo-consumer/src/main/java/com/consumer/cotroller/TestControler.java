package com.consumer.cotroller;

import com.bgiyj.annotation.RpcReference;
import com.bgiyj.core.common.entity.ServerNode;
import com.bgiyj.core.holder.RemoteServerHolder;
import com.rpc.api.UserService;
import com.rpc.dto.UserDto;
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

