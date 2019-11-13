package com.services;

import com.model.Column;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

abstract class SqlService {
    protected Connection connection;

    public SqlService(Connection connection) {
        this.connection = connection;
    }

    public Connection getConnection() {
        return connection;
    }

    public void setConnection(Connection connection) {
        this.connection = connection;
    }

    abstract List<String> getListTableNames(String database) throws SQLException;

    public List<Column> getListOutputColumns(String tableName, String database) throws SQLException {
        List<Column> listOutputColumns = new ArrayList<>();
        String sqlString = "Select COLUMN_NAME, DATA_TYPE, CHARACTER_MAXIMUM_LENGTH " +
                "from INFORMATION_SCHEMA.COLUMNS where TABLE_NAME = ? and TABLE_SCHEMA = ?";
        PreparedStatement preparedStatement = this.connection.prepareStatement(sqlString);
        preparedStatement.setString(1, tableName);
        preparedStatement.setString(2, database);
        ResultSet rs = preparedStatement.executeQuery();
        while (rs.next()) {
            Column column = new Column();
            column.setName(rs.getString(1));
            column.setDataType(rs.getString(2));
            Object length = rs.getObject(3);
            int columnLength;
            try {
                columnLength = (int) length;
            } catch (Exception e) {
                columnLength = 100;
                System.out.println("Error when cast to int");
            }
            column.setLength(columnLength);
            listOutputColumns.add(column);
        }

        return listOutputColumns;
    }

    public List<List<String>> getDataFromTable(String tableName) throws SQLException {
        List<List<String>> listDataFromTables = new ArrayList<>();
        String sqlString = "Select * from " + tableName;
        PreparedStatement preparedStatement = this.connection.prepareStatement(sqlString);
        ResultSet rs = preparedStatement.executeQuery();
        ResultSetMetaData resultSetMetaData = rs.getMetaData();
        int numberOfColumn = resultSetMetaData.getColumnCount();
        while (rs.next()) {
            List<String> listOneRow = new ArrayList<>();
            for (int i = 1; i <= numberOfColumn; i++) {
                listOneRow.add(String.valueOf(rs.getObject(i)));
            }
            listDataFromTables.add(listOneRow);
        }
        return listDataFromTables;
    }
}
