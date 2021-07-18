package com.study.cloud.client;

import com.study.cloud.commons.Store;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import java.util.List;

/**
 * @author Hash
 * @date 2021年07月18日 10:28
 */
// stores表示是一个客户端名称
//@FeignClient("goods-service")
// fallback hystrix的fallback回调
@FeignClient(name="goods-service", fallback = StoreServiceFallback.class)
// 更丰富的配置，StoresConfiguration会覆盖默认配置
//@FeignClient(contextId = "barClient", name = "goods-service", configuration = StoresConfiguration.class)
public interface StoreService {
    @RequestMapping(method = RequestMethod.GET, value = "/stores")
    List<Store> getStores();

    @RequestMapping(method = RequestMethod.GET, value = "/store/{storeId}")
    Store getStore(@PathVariable("storeId") Integer storeId);

    @RequestMapping(method = RequestMethod.GET, value = "/sleep/{time}")
    String sleep(@PathVariable("time") Long storeId);

    @RequestMapping(method = RequestMethod.POST, value = "/stores/{storeId}", consumes = "application/json")
    Store update(@PathVariable("storeId") Integer storeId, Store store);
}
