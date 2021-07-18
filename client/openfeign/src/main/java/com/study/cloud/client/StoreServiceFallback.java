package com.study.cloud.client;

import com.study.cloud.commons.Store;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.List;

/**
 * 开启hystrix后的fallback回调，降级处理
 * @author Hash
 * @date 2021年07月18日 10:31
 */
@Component
public class StoreServiceFallback implements StoreService {
    @Override
    public List<Store> getStores() {
        return Collections.emptyList();
    }

    @Override
    public Store getStore(Integer storeId) {
        return null;
    }

    @Override
    public String sleep(Long storeId) {
        return "sleep fall back";
    }

    @Override
    public Store update(Integer storeId, Store store) {
        return null;
    }
}
