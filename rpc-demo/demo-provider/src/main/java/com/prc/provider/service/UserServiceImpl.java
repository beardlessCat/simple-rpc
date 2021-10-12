package com.prc.provider.service;

import com.bgiyj.annotation.RpcService;
import com.rpc.api.UserService;
import com.rpc.dto.UserDto;
import org.springframework.stereotype.Component;

@Component
@RpcService
public class UserServiceImpl implements UserService {
    @Override
    public UserDto getUser(String id) {
        UserDto userDto = new UserDto("小明",10,"男","山东济南！");
        return userDto;
    }
}
