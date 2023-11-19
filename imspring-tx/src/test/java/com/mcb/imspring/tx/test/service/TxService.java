package com.mcb.imspring.tx.test.service;

import com.mcb.imspring.core.annotation.Component;
import com.mcb.imspring.tx.annotation.Transactional;

@Component
public class TxService {

    @Transactional
    public void hello() {
        System.out.println("hello tx");
    }
}
