package com.prc.provider.service;

import com.bgiyj.core.common.annotation.RpcService;
import com.rpc.api.UserService;
import com.rpc.dto.UserDto;
import org.springframework.stereotype.Component;

@Component
@RpcService
public class UserServiceImpl implements UserService {
    @Override
    public UserDto getUser(String id) {
        return null;
    }
}
