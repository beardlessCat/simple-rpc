package com.bgiyj.config;

import com.bgiyj.load.balance.LoadBalance;
import com.bgiyj.load.balance.RandomLoadBalance;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class LoadBalanceConfig {
    @Bean
    LoadBalance loadBalance(){
        return new RandomLoadBalance();
    }
}
