package com.study.cloud.eureka;

import com.netflix.appinfo.InstanceInfo;
import com.netflix.discovery.EurekaClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.ServiceInstance;
import org.springframework.cloud.client.loadbalancer.LoadBalanced;
import org.springframework.cloud.client.loadbalancer.LoadBalancerClient;
import org.springframework.cloud.client.loadbalancer.LoadBalancerRequest;
import org.springframework.cloud.netflix.eureka.EnableEurekaClient;
import org.springframework.context.annotation.Bean;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

import java.io.IOException;

/**
 * @author Hash
 * @date 2021年07月17日 21:08
 */
@RestController
@EnableEurekaClient
@SpringBootApplication
public class LoadbalancerConsumer {

    private static final String ORDER_SERVICE_URL = "http://GOODS-SERVICE";

    public static void main(String[] args) {
        SpringApplication.run(LoadbalancerConsumer.class, args);
    }

    // 使用eurekaClient也能使用Loadbalancer
    @Autowired
    private EurekaClient eurekaClient;

    @Autowired
    private LoadBalancerClient lb;

    @Autowired
    RestTemplate restTemplate;

    // 带上负载均衡的RestTemplate
    @LoadBalanced
    @Bean
    public RestTemplate bean(){
        return new RestTemplate();
    }

    @RequestMapping("/")
    public String home() {
        return "Welcome to eureka consummer!";
    }

    @RequestMapping("/consummer")
    public String consummer() {
        // 带有负载均衡的eurekaClient
        InstanceInfo info = eurekaClient.getNextServerFromEureka("goods-service",false);
        return info.getHomePageUrl();
    }

    // 带有负载均衡效果的restTemplate
    @RequestMapping("/provider/info")
    public String providerInfo() {
        // 通过Eureka服务标识符(VIP) ORDER_SERVICE_URL 访问服务
        System.out.println(lb);
        try {
            final String execute = lb.execute("goods-service", new LoadBalancerRequest<String>() {
                @Override
                public String apply(ServiceInstance instance) throws Exception {
                    final String serviceId = instance.getServiceId();
                    final int port = instance.getPort();
                    final String url = "http://" + serviceId + ":" + port + "/server/info";
                    final String result = restTemplate.getForObject(url, String.class);
                    return result;
                }
            });
            System.out.println(execute);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return restTemplate.getForObject(ORDER_SERVICE_URL+"/server/info", String.class);
    }
}
