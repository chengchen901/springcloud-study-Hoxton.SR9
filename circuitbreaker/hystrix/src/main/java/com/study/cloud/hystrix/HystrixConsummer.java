package com.study.cloud.hystrix;

import com.netflix.appinfo.InstanceInfo;
import com.netflix.discovery.EurekaClient;
import com.netflix.hystrix.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.circuitbreaker.Customizer;
import org.springframework.cloud.client.circuitbreaker.EnableCircuitBreaker;
import org.springframework.cloud.client.loadbalancer.LoadBalanced;
import org.springframework.cloud.netflix.eureka.EnableEurekaClient;
import org.springframework.cloud.netflix.hystrix.HystrixCircuitBreakerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

@SpringBootApplication
@EnableEurekaClient
@RestController
@EnableCircuitBreaker
public class HystrixConsummer {

    public static void main(String[] args) {
        SpringApplication.run(HystrixConsummer.class, args);
    }
    
    @Autowired
    private EurekaClient eurekaClient;
    
    @Autowired
    RestTemplate restTemplate;
    
    @Bean
    @LoadBalanced   // 通过VIP地址访问
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
        String url = "http://"+info.getVIPAddress()+"/goods/" + 123;
        String data = restTemplate.getForObject(url, String.class);
        return data;
    }
    
    @RequestMapping("/provider/info")
    public String providerInfo() {
        InstanceInfo info = eurekaClient.getNextServerFromEureka("goods-service",false);
        return info.getHomePageUrl();
    }

    /**
     * 配置默认的自定义HystrixCircuitBreakerFactory。
     * @return
     */
//    @Bean
    public Customizer<HystrixCircuitBreakerFactory> defaultConfig() {
        int coreSize = 20;
        return factory -> factory.configureDefault(
                id -> HystrixCommand.Setter
                // 必填项。指定命令分组名，主要意义是用于统计
                .withGroupKey(HystrixCommandGroupKey.Factory.asKey(id))
                // 依赖名称（如果是服务调用，这里就写具体的接口名， 如果是自定义的操作，就自己命令），默认是command实现类的类名。 熔断配置就是根据这个名称
                .andCommandKey(HystrixCommandKey.Factory.asKey(id))
                // 线程池命名，默认是HystrixCommandGroupKey的名称。 线程池配置就是根据这个名称
                .andThreadPoolKey(HystrixThreadPoolKey.Factory.asKey(id+"-Thread"))
                // command 熔断相关参数配置
                .andCommandPropertiesDefaults(
                        HystrixCommandProperties.Setter()
                                // 请求错误统计量度值
                                .withCircuitBreakerRequestVolumeThreshold(5)
                                // 错误请求触发的百分比数
                                .withCircuitBreakerErrorThresholdPercentage(1)
                                // 熔断时间（熔断开启后，各5秒后进入半开启状态，试探是否恢复正常）
                                .withCircuitBreakerSleepWindowInMilliseconds(5000)
                                // 配置隔离方式：默认采用线程池隔离。还有一种信号量隔离方式,
                                //.withExecutionIsolationStrategy(HystrixCommandProperties.ExecutionIsolationStrategy.SEMAPHORE)
                                // 超时时间500毫秒
                                .withExecutionTimeoutInMilliseconds(500)
                        // 信号量隔离的模式下，最大的请求数。和线程池大小的意义一样
                        // .withExecutionIsolationSemaphoreMaxConcurrentRequests(2)
                )
                // 设置线程池参数
                .andThreadPoolPropertiesDefaults(HystrixThreadPoolProperties.Setter()
                        // 线程池大小
                        .withCoreSize(coreSize))
        );
    }

}
