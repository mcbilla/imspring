package com.mcb.imspring.tx.jdbc;

import com.mcb.imspring.tx.exception.DataAccessException;
import com.sun.istack.internal.Nullable;

import java.sql.SQLException;
import java.sql.Statement;

@FunctionalInterface
public interface StatementCallback<T> {
    @Nullable
    T doInStatement(Statement stmt) throws SQLException, DataAccessException;
}
