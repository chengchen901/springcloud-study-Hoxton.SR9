package com.study.cloud.config;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.config.server.EnableConfigServer;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author Hash
 * @date 2021年07月18日 15:30
 */
@SpringBootApplication
@EnableDiscoveryClient
@EnableConfigServer
@RestController
public class ConfigServer {
    public static void main(String[] args) {
        SpringApplication.run(ConfigServer.class, args);
    }

    @RequestMapping("/")
    public String home() {
        return "Hello, welcome to config server";
    }
}
