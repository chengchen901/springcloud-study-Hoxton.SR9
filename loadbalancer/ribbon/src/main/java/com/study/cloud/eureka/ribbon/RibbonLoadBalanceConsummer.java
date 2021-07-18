package com.study.cloud.eureka.ribbon;

import com.netflix.appinfo.InstanceInfo;
import com.netflix.discovery.EurekaClient;
import com.study.cloud.eureka.ribbonconf.GoodsServiceLoadbalancerConfig;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.ServiceInstance;
import org.springframework.cloud.client.loadbalancer.LoadBalanced;
import org.springframework.cloud.client.loadbalancer.LoadBalancerClient;
import org.springframework.cloud.netflix.eureka.EnableEurekaClient;
import org.springframework.cloud.netflix.ribbon.RibbonClient;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Profile;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

@SpringBootApplication
@EnableEurekaClient
@RestController
@Profile("ribbon")
// name必须为VIPAdress，否则对应接口不能起作用，比如这里的goods-service
@RibbonClient(name="goods-service", configuration = GoodsServiceLoadbalancerConfig.class)
public class RibbonLoadBalanceConsummer {
    private static final String GOODS_SERVICE_URL = "http://goods-service";

    public static void main(String[] args) {
        SpringApplication.run(RibbonLoadBalanceConsummer.class, args);
    }
    // 第一种方式，直接使用Netflix的EurekaClient
    @Autowired
    private EurekaClient eurekaClient;
    // 第二种方式，使用SpringCloud的LoadBalancerClient，脱离Netflix
    @Autowired
    private LoadBalancerClient loadBalancerClient;
    
    // 第三种方式，使用带@LoadBalanced注解的restTemplate
    // 还有第四种方式，使用feign，后面再做解释
    @Autowired
    RestTemplate restTemplate;
    
    
    @Bean
    @LoadBalanced
    public RestTemplate bean(){
        return new RestTemplate();
    }
    
    @RequestMapping("/")
    public String home() {
        return "Welcome to eureka consummer!";
    }

    @RequestMapping("/consummer")
    public String consummer() {
        InstanceInfo info = eurekaClient.getNextServerFromEureka("goods-service",false);
        String url = "http://"+info.getVIPAddress()+"/server/info";
        String data = restTemplate.getForObject(url, String.class);
        return data;
    }
    
    @RequestMapping("/loadbalancer")
    public String loadbalancer() {
        ServiceInstance instance = loadBalancerClient.choose("goods-service");
        return instance.getUri().toString();
    }
    
    @RequestMapping("/provider/info")
    public String providerInfo() {
        return restTemplate.getForObject(GOODS_SERVICE_URL +"/server/info", String.class);
    }
}
