package com.provider.service.impl;

import com.api.UserService;
import com.common.annotation.RpcService;
import com.dto.UserDto;
import org.springframework.stereotype.Repository;

@RpcService
@Repository// fixme 使用@RpcService自动进行bean注入
public class UserServiceImpl implements UserService {
    @Override
    public UserDto getUser(String id) {
        return new UserDto("小明",10,"男","北京");
    }
}
