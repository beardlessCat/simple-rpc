package com.bgiyj.config;

import com.bgiyj.factory.MethodHandlerFactory;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConditionalOnProperty(prefix = "rpc",name = "role",havingValue = "consumer")
public class RpcConfig {
    @Bean
    MethodHandlerFactory methodHandlerFactory(){
        return new MethodHandlerFactory();
    }
}
