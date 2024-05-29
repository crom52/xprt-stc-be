package com.stc;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
//@EnableJpaAuditing
//@EnableConfigurationProperties
@EnableScheduling
public class STCExplorerApplication {

    public static void main(String[] args) {
        SpringApplication.run(STCExplorerApplication.class, args);
    }
}
