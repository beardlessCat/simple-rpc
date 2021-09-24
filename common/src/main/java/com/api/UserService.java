package com.api;

import com.dto.UserDto;

public interface UserService {
    UserDto getUser(String id);
}
