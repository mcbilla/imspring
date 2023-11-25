package com.mcb.imspring.tx.jdbc;

import com.mcb.imspring.tx.exception.DataAccessException;
import com.sun.istack.internal.Nullable;

import java.util.List;

/**
 * JDBC方法接口，暂时只支持静态SQL，不支持预编译SQL
 */
public interface JdbcOperations {

    //-------------------------------------------------------------------------
    // Methods dealing with static SQL (java.sql.Statement)
    //-------------------------------------------------------------------------

    <T> T execute(StatementCallback<T> action) throws DataAccessException;

    <T> T queryForObject(String sql, RowMapper<T> rowMapper) throws DataAccessException;

    @Nullable
    <T> T queryForObject(String sql, Class<T> requiredType) throws DataAccessException;

    <T> List<T> queryForList(String sql, Class<T> elementType) throws DataAccessException;

    int update(String sql) throws DataAccessException;

}
