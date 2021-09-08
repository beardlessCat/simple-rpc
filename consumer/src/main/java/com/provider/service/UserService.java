package com.provider.service;

import com.common.annotation.RpcService;

@RpcService
public interface UserService {
    void getUser(String name ,String id);
}
