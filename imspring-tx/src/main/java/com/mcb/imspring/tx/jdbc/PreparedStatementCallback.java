package com.mcb.imspring.tx.jdbc;

import com.mcb.imspring.tx.exception.DataAccessException;

import java.sql.PreparedStatement;
import java.sql.SQLException;

@FunctionalInterface
public interface PreparedStatementCallback<T> {

    T doInPreparedStatement(PreparedStatement ps) throws SQLException, DataAccessException;

}
