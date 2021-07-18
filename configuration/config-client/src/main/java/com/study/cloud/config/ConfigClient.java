package com.study.cloud.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author Hash
 * @date 2021年07月18日 15:53
 */
@SpringBootApplication
@RestController
@EnableDiscoveryClient
// 表示当前类是一个可刷新区域
@RefreshScope
public class ConfigClient {
    @Value("${user.name}")
    private String name;

    @RequestMapping("/")
    public String home() {
        return "Hello" + name + " ! Welcome to config client.";
    }

    public static void main(String[] args) {
        SpringApplication.run(ConfigClient.class, args);
    }
}
