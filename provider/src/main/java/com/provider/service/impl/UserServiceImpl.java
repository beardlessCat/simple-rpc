package com.provider.service.impl;

import com.api.UserService;
import com.common.annotation.RpcService;

@RpcService
public class UserServiceImpl implements UserService {
    @Override
    public void getUser(String name, String id) {
        System.out.println(name + ":" + id);
    }
}
