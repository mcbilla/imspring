package com.mcb.imspring.tx.test.service;

import com.mcb.imspring.core.annotation.Component;

@Component
public class TxService {

    public void hello() {
        System.out.println("hello tx");
    }
}
