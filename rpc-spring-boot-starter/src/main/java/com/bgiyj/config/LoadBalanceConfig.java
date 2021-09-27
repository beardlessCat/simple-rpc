package com.bgiyj.config;

import com.bgiyj.load.balance.LoadBalance;
import com.bgiyj.load.balance.RandomLoadBalance;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConditionalOnProperty(prefix = "rpc",name = "role",havingValue = "consumer")
public class LoadBalanceConfig {
    @Bean
    LoadBalance loadBalance(){
        return new RandomLoadBalance();
    }
}
