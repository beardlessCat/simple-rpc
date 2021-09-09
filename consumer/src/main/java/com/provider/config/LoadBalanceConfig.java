package com.provider.config;

import com.provider.load.balance.LoadBalance;
import com.provider.load.balance.RandomLoadBalance;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class LoadBalanceConfig {
    @Bean
    LoadBalance loadBalance(){
        return new RandomLoadBalance();
    }
}
