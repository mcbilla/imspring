package com.mcb.imspring.tx.test.service;

import com.mcb.imspring.core.annotation.Autowired;
import com.mcb.imspring.core.annotation.Component;
import com.mcb.imspring.tx.annotation.Transactional;
import com.mcb.imspring.tx.jdbc.JdbcTemplate;
import com.mcb.imspring.tx.test.entity.User;
import com.mcb.imspring.tx.transaction.td.DefaultTransactionDefinition;
import com.mcb.imspring.tx.transaction.td.TransactionDefinition;
import com.mcb.imspring.tx.transaction.tm.PlatformTransactionManager;
import com.mcb.imspring.tx.transaction.ts.DefaultTransactionStatus;
import com.mcb.imspring.tx.transaction.ts.TransactionStatus;

@Component
public class TxService {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Autowired
    private PlatformTransactionManager platformTransactionManager;

    public User query() {
        String sql = "select * from user where id = 1";
        return jdbcTemplate.queryForObject(sql, User.class);
    }

    public int update() {
        String sql = "insert into user(name, age, add_time) values('aaa', 18, now())";
        return jdbcTemplate.update(sql);
    }

    @Transactional(rollbackFor = Exception.class)
    public Object declareTx() {
        String sql = "insert into user(name, age, add_time) values('aaa', 18, now())";
        int update = jdbcTemplate.update(sql);
//        System.out.println(1/0);
        return update;
    }

    public Object programTx() {
        DefaultTransactionDefinition def = new DefaultTransactionDefinition();
        def.setPropagationBehavior(TransactionDefinition.PROPAGATION_REQUIRES_NEW);
        TransactionStatus status = platformTransactionManager.getTransaction(def);

        try {
            String sql = "insert into user(name, age, add_time) values('aaa', 18, now())";
            int res = jdbcTemplate.update(sql);
            System.out.println(1/0);
            platformTransactionManager.commit(status);
            System.out.println("数据插入成功");
            return res;
        } catch (Exception e) {
            System.err.println("数据插入失败 " + e);
            platformTransactionManager.rollback(status);
            return null;
        }
    }
}
