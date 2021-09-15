package com.api;

import com.common.annotation.RpcService;
import com.dto.UserDto;

@RpcService
public interface UserService {
    UserDto getUser(String id);
}
