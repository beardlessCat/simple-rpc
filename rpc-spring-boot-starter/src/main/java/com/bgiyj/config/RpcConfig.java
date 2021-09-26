package com.bgiyj.config;

import com.bgiyj.factory.MethodHandlerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RpcConfig {
    @Bean
    MethodHandlerFactory methodHandlerFactory(){
        return new MethodHandlerFactory();
    }
}
