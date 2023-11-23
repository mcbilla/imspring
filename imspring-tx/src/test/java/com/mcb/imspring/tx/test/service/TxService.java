package com.mcb.imspring.tx.test.service;

import com.mcb.imspring.core.annotation.Autowired;
import com.mcb.imspring.core.annotation.Component;
import com.mcb.imspring.tx.annotation.Transactional;
import com.mcb.imspring.tx.jdbc.JdbcTemplate;

@Component
public class TxService {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Transactional
    public void hello() {
        String sql = "insert into user(name, age, add_time) values('aaa', 18, now())";
        jdbcTemplate.update(sql);
//        System.out.println(1/0);
        System.out.println("数据插入成功");
    }
}
