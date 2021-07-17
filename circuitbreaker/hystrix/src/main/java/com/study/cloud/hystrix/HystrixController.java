package com.study.cloud.hystrix;

import com.netflix.appinfo.InstanceInfo;
import com.netflix.discovery.EurekaClient;
import com.netflix.hystrix.contrib.javanica.annotation.DefaultProperties;
import com.netflix.hystrix.contrib.javanica.annotation.HystrixCommand;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;


@RestController
@DefaultProperties(defaultFallback = "globalFallback")
public class HystrixController {
    @Autowired
    private EurekaClient eurekaClient;
    @Autowired
    RestTemplate restTemplate;
    
    // fallbackMethod方法的必须与在当前类中，且方法签名一致与当前方法一致
    /*@HystrixCommand(fallbackMethod = "sleepFallback", commandProperties = {
            @HystrixProperty(name = "execution.isolation.strategy", value = "SEMAPHORE")})*/
    @HystrixCommand(commandKey = "sleep", fallbackMethod = "sleepFallback")
    @RequestMapping("/sleep/{time}")
    public Object sleep(@PathVariable("time") long time) {
        InstanceInfo info = eurekaClient.getNextServerFromEureka("goods-service",false);
        String url = "http://"+info.getVIPAddress()+"/sleep/" + time;
        String data = restTemplate.getForObject(url, String.class);
        return data;
    }
    
    // sleep方法的fallback，签名保持一致，且在同一个类中
    public Object sleepFallback(long time) {
        return "Break fallback";
    }


    @HystrixCommand
    @RequestMapping("/cost/{time}")
    public Object cost(@PathVariable("time") long time) {
        System.out.println("cost time request.");
        InstanceInfo info = eurekaClient.getNextServerFromEureka("goods-service",false);
        String url = "http://"+info.getVIPAddress()+"/sleep/" + time;
        String data = restTemplate.getForObject(url, String.class);
        return data;
    }
    public Object globalFallback() {
        return "Global fallback";
    }
}
