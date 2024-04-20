package com.stc.xprt;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
//@EnableJpaAuditing
//@EnableConfigurationProperties
@EnableScheduling
public class STCExplorerApplication {

    public static void main(String[] args) {
        String homeDirectory = System.getProperty("user.home");
        System.out.println("Directory: " + homeDirectory);
        SpringApplication.run(STCExplorerApplication.class, args);
    }
}
