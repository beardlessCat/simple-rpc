package com.provider.discovery;

import com.provider.client.ConsumerClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;

@Component
public class ServiceDiscovery {
    @Autowired
    private ConsumerClient consumerClient ;
    @PostConstruct
    public void init(){
        consumerClient.startClient();
    }
}

