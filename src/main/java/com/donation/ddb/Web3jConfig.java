package com.donation.ddb;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.web3j.protocol.Web3j;
import org.web3j.protocol.http.HttpService;


@Configuration
public class Web3jConfig {
    @Bean
    public Web3j web3j(){
        return Web3j.build(new HttpService("https://sepolia.infura.io/v3/MY_INFURA_KEY"));
    }
}
