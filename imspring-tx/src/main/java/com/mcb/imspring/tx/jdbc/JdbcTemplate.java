package com.mcb.imspring.tx.jdbc;

import com.mcb.imspring.core.context.InitializingBean;
import com.mcb.imspring.core.utils.Assert;
import com.mcb.imspring.core.utils.CollectionUtils;
import com.mcb.imspring.tx.exception.DataAccessException;
import com.mcb.imspring.tx.sync.TransactionSynchronizationManager;
import com.sun.istack.internal.Nullable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class JdbcTemplate implements JdbcOperations, InitializingBean {

    private final Logger logger = LoggerFactory.getLogger(getClass());

    @Nullable
    private DataSource dataSource;

    public JdbcTemplate() {
    }

    public JdbcTemplate(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    public void setDataSource(@Nullable DataSource dataSource) {
        this.dataSource = dataSource;
        afterPropertiesSet();
    }

    @Nullable
    public DataSource getDataSource() {
        return this.dataSource;
    }

    protected DataSource obtainDataSource() {
        DataSource dataSource = getDataSource();
        Assert.state(dataSource != null, "No DataSource set");
        return dataSource;
    }

    protected Connection getConnection(DataSource dataSource) throws DataAccessException {
        try {
            return dataSource.getConnection();
        } catch (SQLException e) {
            throw new DataAccessException(e);
        }
    }

    @Override
    public void afterPropertiesSet() {
        if (getDataSource() == null) {
            throw new IllegalArgumentException("Property 'dataSource' is required");
        }
    }

    private static String getSql(Object sqlProvider) {
        if (sqlProvider instanceof SqlProvider) {
            return ((SqlProvider) sqlProvider).getSql();
        }
        return null;
    }

    protected void releaseConnection(Connection con, DataSource dataSource) {
        if (con == null) {
            return;
        }
        if (dataSource != null) {
            ConnectionHolder conHolder = (ConnectionHolder) TransactionSynchronizationManager.getResource(dataSource);
            if (conHolder != null) {
                Connection heldCon = conHolder.getConnection();
                if (heldCon == con || heldCon.equals(con)) {
                    // It's the transactional Connection: Don't close it.
                    conHolder.released();
                    return;
                }
            }
        }
        try {
            con.close();
        } catch (SQLException ex) {
            logger.debug("Could not close JDBC Connection", ex);
        } catch (Throwable ex) {
            logger.debug("Unexpected exception on closing JDBC Connection", ex);
        }
    }

    @Override
    public <T> T execute(StatementCallback<T> action) throws DataAccessException {
        Assert.notNull(action, "Callback object must not be null");
        Connection connection = getConnection(obtainDataSource());
        try (Statement stmt = connection.createStatement();) {
            return action.doInStatement(stmt);
        } catch (SQLException e) {
            String sql = getSql(action);
            throw new DataAccessException(String.format("StatementCallback failed", sql), e);
        } finally {
            releaseConnection(connection, dataSource);
        }
    }

    protected <T> List<T> query(String sql, RowMapper<T> rowMapper) {
        Assert.notNull(sql, "SQL must not be null");

        class QueryStatementCallback implements StatementCallback<List<T>>, SqlProvider {

            @Override
            public List<T> doInStatement(Statement stmt) throws SQLException, DataAccessException {
                List<T> results = new ArrayList<>();
                try (ResultSet rs = stmt.executeQuery(sql)) {
                    while (rs.next()) {
                        results.add(rowMapper.mapRow(rs, rs.getRow()));
                    }
                }
                return results;
            }

            @Override
            public String getSql() {
                return sql;
            }
        }
        return execute(new QueryStatementCallback());
    }

    @Override
    public <T> T queryForObject(String sql, RowMapper<T> rowMapper) throws DataAccessException {
        List<T> results = query(sql, rowMapper);
        return nullableSingleResult(results);
    }

    @Override
    public <T> T queryForObject(String sql, Class<T> requiredType) throws DataAccessException {
        List<T> results = query(sql, new BeanPropertyRowMapper<>(requiredType));
        return nullableSingleResult(results);
    }

    @Override
    public <T> List<T> queryForList(String sql, Class<T> elementType) throws DataAccessException {
        return query(sql, new BeanPropertyRowMapper<>(elementType));
    }

    public <T> T nullableSingleResult(@Nullable Collection<T> results) {
        if (CollectionUtils.isEmpty(results) || results.size() > 1) {
            throw new DataAccessException(String.format("Incorrect result size: expected " + 1 + ", actual " + results.size()));
        }
        return results.iterator().next();
    }

    public int update(final String sql) throws DataAccessException {
        Assert.notNull(sql, "SQL must not be null");
        logger.debug("Executing SQL update [" + sql + "]");

        return 0;
    }
}
