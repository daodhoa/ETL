package com.dataflow;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name = "connectionManagers")
public class ConnectionManager {
    private ExcelManager excelManager;
    private SqlServerManager sqlServerManager;

    public ConnectionManager() {
        this.excelManager = null;
        this.sqlServerManager = null;
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
}
