package com.services;

import com.connection.ExcelConnection;
import com.connection.SqlServerConnection;
import com.dataflow.DataFlow;
import com.dataflow.ExcelManager;
import com.dataflow.SqlServerManager;
import com.dataflow.components.*;
import com.expression.ExpressionHelper;
import com.model.Column;
import com.model.Excel;
import com.model.SqlServer;
import sample.Session;

import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ExecuteDataflow {
    DataFlow dataFlow = Session.getDataFlow();

    private List<Map<String, String>> getDataFormSource() {
        List<Map<String, String>> listData = new ArrayList<>();

        SourceInterface source =  dataFlow.getExecutables().getPineline().getComponents().getSource();
        if (source instanceof ExcelSourceComponent) {
            ExcelManager excelManager = dataFlow.getConnectionManager().getExcelManager();
            Excel excel = excelManager.getExcel();
            ExcelConnection excelConnection = new ExcelConnection(excel.getFilePath());
            try {
                List<List<String>> listDataFromSheet = excelConnection.getDataFromSheet(excel.getSheetIndex());
                List<Column> listOutputColumns = source.getOutputColumns();
                int i = 0;
                if (excel.isFirstRow()) {
                    i = 1;
                }
                for ( ; i < listDataFromSheet.size(); i ++) {
                    Map<String, String> mapOneRow = new HashMap<>();
                    List<String> listOneRow = listDataFromSheet.get(i);
                    for (int j = 0; j < listOutputColumns.size(); j ++) {
                        mapOneRow.put(listOutputColumns.get(j).getId(), listOneRow.get(j));
                    }
                    listData.add(mapOneRow);
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        if (source instanceof SqlServerSource) {
            SqlServerManager sqlServerManager = dataFlow.getConnectionManager().getSqlServerManager();
            SqlServer sqlServer = sqlServerManager.getSqlServer();
            SqlServerConnection sqlServerConnection = new SqlServerConnection(sqlServer.getHostname(),
                    sqlServer.getUsername(), sqlServer.getPassword());
            sqlServerConnection.setDatabaseName(sqlServer.getDatabase());
            try {
                Connection connection = sqlServerConnection.getConnection();
                SqlServerService sqlServerService = new SqlServerService(connection);
                String tableName = ((SqlServerSource) source).getTableName();
                List<List<String>> listDataFromTable = sqlServerService.getDataFromTable(tableName);

                List<Column> listOutputColumns = source.getOutputColumns();

                for (int i = 0; i < listDataFromTable.size(); i ++) {
                    Map<String, String> mapOneRow = new HashMap<>();
                    List<String> listOneRow = listDataFromTable.get(i);
                    for (int j = 0; j < listOutputColumns.size(); j ++) {
                        mapOneRow.put(listOutputColumns.get(j).getId(), listOneRow.get(j));
                    }
                    listData.add(mapOneRow);
                }
            } catch (ClassNotFoundException e) {
                e.printStackTrace();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }

        return listData;
    }

    private List<Map<String, String>> getInputToDestination(List<Map<String, String>> listDataSource) {
        DerivedColumn derivedColumn = dataFlow.getExecutables().getPineline().getComponents().getDerivedColumn();

        List<Map<String, String>> resultOutputData = new ArrayList<>();
        listDataSource.forEach(mapOneRow -> {
            resultOutputData.add(mapOneRow);
        });
        if (derivedColumn != null) {
            List<Column> outputColumns = derivedColumn.getOutputColumns();
            if (outputColumns.size() != 0) {
                for (int i = 0; i < listDataSource.size(); i ++) {
                    Map<String, String> mapOneRow = listDataSource.get(i);

                    for (int j = 0; j < outputColumns.size(); j++) {
                        Column column = outputColumns.get(j);
                        mapOneRow = ExpressionHelper.executeExpression(mapOneRow, column);
                    }
                    resultOutputData.set(i, mapOneRow);
                }
            }
        }
        return resultOutputData;
    }

    public void execute() {
        List<Map<String, String>> listData = this.getDataFormSource();
        List<Map<String, String>> listInputDataToDestination = getInputToDestination(listData);

        DestinationInterface destination = Session.getDataFlow().getExecutables()
                .getPineline().getComponents().getDestination();
        if (destination instanceof SqlServerDestination) {
            List<Column> listInputColumns = ((SqlServerDestination) destination).getInputColumns();
            String tableName = ((SqlServerDestination) destination).getTableName();
            String columnToInsert = "";
            String valueToInsert = "";
            for (int i = 0; i < listInputColumns.size(); i++) {
                columnToInsert += "[" +listInputColumns.get(i).getName() + "]" + ", ";
                valueToInsert += "?, ";
            }
            columnToInsert = columnToInsert.substring(0, columnToInsert.length() - 2);
            valueToInsert = valueToInsert.substring(0, valueToInsert.length() - 2);

            String sqlString = "insert into " + tableName + " (" + columnToInsert + ")"
                    + " values (" + valueToInsert +")";

            SqlServer sqlServer = dataFlow.getConnectionManager().getSqlServerManagerDestination().getSqlServer();
            SqlServerConnection sqlServerConnection =
                    new SqlServerConnection(sqlServer.getHostname(), sqlServer.getUsername(), sqlServer.getPassword());
            sqlServerConnection.setDatabaseName(sqlServer.getDatabase());
            try {
                Connection connection = sqlServerConnection.getConnection();
                PreparedStatement preparedStatement = connection.prepareStatement(sqlString);

                connection.setAutoCommit(false);
                try {
                    for (int i = 0; i < listInputDataToDestination.size(); i++) {
                        Map<String, String> mapOneRow = listInputDataToDestination.get(i);
                        System.out.println("Khong thuc hien");
                        for (int j = 0; j < listInputColumns.size(); j ++) {
                            preparedStatement.setObject(j + 1, mapOneRow.get(listInputColumns.get(j).getLinearId()));
                        }

                        preparedStatement.addBatch();
                        if (i == 100 || i == listInputDataToDestination.size() - 1) {
                            System.out.println("Thuc hien query");
                            preparedStatement.executeBatch();
                        }
                    }
                } catch (Exception e) {
                    connection.rollback();
                }
                connection.commit();
                connection.close();

            } catch (ClassNotFoundException e) {
                e.printStackTrace();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }

        if (destination instanceof ExcelDestination) {
            List<Column> listInputColumns =  ((ExcelDestination) destination).getInputColumns();
            Excel excel = dataFlow.getConnectionManager().getExcelManagerDestination().getExcel();
            ExcelConnection excelConnection = new ExcelConnection(excel.getFilePath());
            try {
                excelConnection.insertDataToSheet(excel.getSheetIndex(), listInputColumns, listInputDataToDestination);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
