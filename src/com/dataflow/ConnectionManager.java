package com.dataflow;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name = "connectionManagers")
public class ConnectionManager {
    private ExcelManager excelManager;
    private SqlServerManager sqlServerManager;
    private SqlServerManager sqlServerManagerDestination;
    private ExcelManager excelManagerDestination;
    private MySqlManager mySqlManager;
    private MySqlManager mySqlManagerDestination;

    public ConnectionManager() {
        this.mySqlManager = null;
        this.excelManager = null;
        this.sqlServerManager = null;
        this.sqlServerManagerDestination = null;
        this.excelManagerDestination = null;
        this.mySqlManagerDestination = null;
    }

    public ExcelManager getExcelManager() {
        return excelManager;
    }

    @XmlElement(name = "connectionManager")
    public void setExcelManager(ExcelManager excelManager) {
        this.excelManager = excelManager;
    }

    public SqlServerManager getSqlServerManager() {
        return sqlServerManager;
    }

    @XmlElement(name = "connectionManager")
    public void setSqlServerManager(SqlServerManager sqlServerManager) {
        this.sqlServerManager = sqlServerManager;
    }

    public SqlServerManager getSqlServerManagerDestination() {
        return sqlServerManagerDestination;
    }
    @XmlElement(name = "connectionManager")
    public void setSqlServerManagerDestination(SqlServerManager sqlServerManagerDestination) {
        this.sqlServerManagerDestination = sqlServerManagerDestination;
    }
    public ExcelManager getExcelManagerDestination() {
        return excelManagerDestination;
    }
    @XmlElement(name = "connectionManager")
    public void setExcelManagerDestination(ExcelManager excelManagerDestination) {
        this.excelManagerDestination = excelManagerDestination;
    }
    public MySqlManager getMySqlManager() {
        return mySqlManager;
    }
    @XmlElement(name = "connectionManager")
    public void setMySqlManager(MySqlManager mySqlManager) {
        this.mySqlManager = mySqlManager;
    }

    public MySqlManager getMySqlManagerDestination() {
        return mySqlManagerDestination;
    }
    @XmlElement(name = "connectionManager")
    public void setMySqlManagerDestination(MySqlManager mySqlManagerDestination) {
        this.mySqlManagerDestination = mySqlManagerDestination;
    }
}
