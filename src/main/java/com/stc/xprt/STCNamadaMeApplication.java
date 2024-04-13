package com.stc.xprt;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
//@EnableJpaAuditing
//@EnableConfigurationProperties
public class STCNamadaMeApplication {

    public static void main(String[] args) {
        String homeDirectory = System.getProperty("user.home");
        System.out.println("Directory: " + homeDirectory);
        SpringApplication.run(STCNamadaMeApplication.class, args);
    }
}
