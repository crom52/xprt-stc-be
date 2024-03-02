package com.stc.namada;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

@SpringBootApplication
//@EnableJpaAuditing
//@EnableConfigurationProperties
public class STCNamadaMeApplication {
    public static void main(String[] args) {
        SpringApplication.run(STCNamadaMeApplication.class, args);
    }
}
