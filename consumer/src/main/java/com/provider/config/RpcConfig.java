package com.provider.config;

import com.provider.factory.MethodHandlerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RpcConfig {
    @Bean
    MethodHandlerFactory methodHandlerFactory(){
        return new MethodHandlerFactory();
    }
}
