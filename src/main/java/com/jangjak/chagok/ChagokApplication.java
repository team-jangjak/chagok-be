package com.jangjak.chagok;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;

@EnableFeignClients
@SpringBootApplication
public class ChagokApplication {

    public static void main(String[] args) {
        SpringApplication.run(ChagokApplication.class, args);
    }

}
