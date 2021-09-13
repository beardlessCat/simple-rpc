package com.provider.service.impl;

import com.api.UserService;
import com.common.annotation.RpcService;
import org.springframework.stereotype.Repository;

@RpcService
@Repository// fixme 使用@RpcService自动进行bean注入
public class UserServiceImpl implements UserService {
    @Override
    public void getUser(String name, String id) {
        System.out.println(name + ":" + id);
    }
}
