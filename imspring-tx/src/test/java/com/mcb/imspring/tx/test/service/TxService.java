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

    public User query() {
        String sql = "select * from user where id = 1";
        return jdbcTemplate.queryForObject(sql, User.class);
    }

    public int update() {
        String sql = "insert into user(name, age, add_time) values('aaa', 18, now())";
        return jdbcTemplate.update(sql);
    }

    @Transactional(rollbackFor = Exception.class)
    public int tx() {
        String sql = "insert into user(name, age, add_time) values('aaa', 18, now())";
        int update = jdbcTemplate.update(sql);
        System.out.println(1/0);
        return update;
    }
}
