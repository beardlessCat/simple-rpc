package com.rpc.api;

import com.rpc.dto.UserDto;

public interface UserService {
    UserDto getUser(String id);
}

