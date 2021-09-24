package com.provider.service.impl;

import com.api.UserService;
import com.common.annotation.RpcService;
import com.dto.UserDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@RpcService
@Component
public class UserServiceImpl implements UserService {
    @Override
    public UserDto getUser(String id) {
        return new UserDto("小明",10,"男","北京");
    }
}
