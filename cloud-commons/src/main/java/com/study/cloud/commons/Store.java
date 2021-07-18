package com.study.cloud.commons;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author Hash
 * @date 2021年07月18日 10:26
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Store {
    private Integer id;
    private String name;
    private String address;
}
