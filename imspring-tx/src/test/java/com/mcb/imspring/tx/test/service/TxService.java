package com.mcb.imspring.tx.test.service;

import com.mcb.imspring.core.annotation.Autowired;
import com.mcb.imspring.core.annotation.Component;
import com.mcb.imspring.tx.annotation.Transactional;
import com.mcb.imspring.tx.jdbc.JdbcTemplate;
import com.mcb.imspring.tx.test.entity.User;

@Component
public class TxService {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Transactional
    public void hello() {
        String sql = "select * from user where id = 1";
        User user = jdbcTemplate.queryForObject(sql, User.class);
        System.out.println(user);
    }
}
