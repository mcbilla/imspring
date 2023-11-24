package com.mcb.imspring.tx.jdbc;

import com.mcb.imspring.core.utils.Assert;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Savepoint;

public class ConnectionHolder {

    public static final String SAVEPOINT_NAME_PREFIX = "SAVEPOINT_";

    private Connection currentConnection;

    private Boolean savepointsSupported;

    private int savepointCounter = 0;

    public ConnectionHolder(Connection connection) {
        Assert.notNull(connection, "Connection must not be null");
        this.currentConnection = connection;
    }

    protected void setConnection(Connection connection) {
        Assert.notNull(connection, "Connection must not be null");
        this.currentConnection = connection;
    }

    protected boolean hasConnection() {
        return (this.currentConnection != null);
    }

    public Connection getConnection() {
        return this.currentConnection;
    }

    public boolean supportsSavepoints() throws SQLException {
        if (this.savepointsSupported == null) {
            this.savepointsSupported = getConnection().getMetaData().supportsSavepoints();
        }
        return this.savepointsSupported;
    }

    public Savepoint createSavepoint() throws SQLException {
        this.savepointCounter++;
        return getConnection().setSavepoint(SAVEPOINT_NAME_PREFIX + this.savepointCounter);
    }

    public void released() {
        if (this.currentConnection != null) {
            this.currentConnection = null;
        }
    }
}
