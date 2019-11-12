package com.dataflow;

import com.dataflow.components.*;

public class Components {
    private ExcelSourceComponent excelSourceComponent;
    private SqlServerSource sqlServerSource;
    private DerivedColumn derivedColumn;
    private SqlServerDestination sqlServerDestination;
    private ExcelDestination excelDestination;

    public Components() {
        this.excelSourceComponent = null;
        this.sqlServerSource = null;
        this.derivedColumn = null;
        this.sqlServerDestination = null;
        this.excelDestination = null;
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

    public DerivedColumn getDerivedColumn() {
        return derivedColumn;
    }

    public void setDerivedColumn(DerivedColumn derivedColumn) {
        this.derivedColumn = derivedColumn;
    }

    public SqlServerDestination getSqlServerDestination() {
        return sqlServerDestination;
    }

    public void setSqlServerDestination(SqlServerDestination sqlServerDestination) {
        this.sqlServerDestination = sqlServerDestination;
    }

    public ExcelDestination getExcelDestination() {
        return excelDestination;
    }

    public void setExcelDestination(ExcelDestination excelDestination) {
        this.excelDestination = excelDestination;
    }

    public DestinationInterface getDestination() {
        if (this.excelDestination != null) {
            return this.excelDestination;
        }
        if (this.sqlServerDestination != null) {
            return this.sqlServerDestination;
        }
        return null;
    }

}
