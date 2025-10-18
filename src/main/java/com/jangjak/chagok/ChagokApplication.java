package com.jangjak.chagok;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.scheduling.annotation.EnableScheduling;

@EnableScheduling
@EnableFeignClients
@SpringBootApplication
@EnableJpaAuditing
public class ChagokApplication {

    public static void main(String[] args) {
        SpringApplication.run(ChagokApplication.class, args);
    }

}
