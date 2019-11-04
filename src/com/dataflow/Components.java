package com.dataflow;

import com.dataflow.components.ExcelSourceComponent;
import com.dataflow.components.SourceInterface;
import com.dataflow.components.SqlServerSource;

public class Components {
    private ExcelSourceComponent excelSourceComponent;
    private SqlServerSource sqlServerSource;

    public Components() {
        this.excelSourceComponent = null;
        this.sqlServerSource = null;
    }

    public ExcelSourceComponent getExcelSourceComponent() {
        return excelSourceComponent;
    }

    public void setExcelSourceComponent(ExcelSourceComponent excelSourceComponent) {
        this.excelSourceComponent = excelSourceComponent;
    }

    public SqlServerSource getSqlServerSource() {
        return sqlServerSource;
    }

    public void setSqlServerSource(SqlServerSource sqlServerSource) {
        this.sqlServerSource = sqlServerSource;
    }

    public SourceInterface getSource() {
        if (this.excelSourceComponent != null) {
            return excelSourceComponent;
        }
        if (this.sqlServerSource != null) {
            return sqlServerSource;
        }
        return null;
    }
}
