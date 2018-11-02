package com.rbx.k8shttpapi;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;

@SpringBootApplication
@EnableFeignClients
public class K8sHttpApiApplication {

    public static void main(String[] args) {
        SpringApplication.run(K8sHttpApiApplication.class, args);
    }
}
