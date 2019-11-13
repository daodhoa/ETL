package com.services;
import com.model.Column;
import java.sql.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class SqlServerService extends SqlService{

    public SqlServerService(Connection connection) {
        super(connection);
    }

    @Override
    List<String> getListTableNames(String database) throws SQLException {
        return null;
    }

    public List<Column> getListOutputColumns(String tableName) throws SQLException {
        List<Column> listOutputColumns = new ArrayList<>();
        String sqlString = "Select COLUMN_NAME, DATA_TYPE, CHARACTER_MAXIMUM_LENGTH " +
                "from INFORMATION_SCHEMA.COLUMNS where TABLE_NAME = ?";
        PreparedStatement preparedStatement = this.connection.prepareStatement(sqlString);
        preparedStatement.setString(1, tableName);
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

    public List<String> getListTableNames() throws SQLException {
        List<String> listTableNames = new ArrayList<>();
        String sqlString = "SELECT TABLE_NAME FROM INFORMATION_SCHEMA.TABLES WHERE TABLE_TYPE = 'BASE TABLE'";
        PreparedStatement preparedStatement = connection.prepareStatement(sqlString);
        ResultSet rs = preparedStatement.executeQuery();

        while (rs.next()) {
            listTableNames.add(rs.getString(1));
        }
        return listTableNames;
    }

    public Map<String, Integer> getListColumns(String tableName) throws SQLException {
        Map<String, Integer> listColumns = new HashMap<>();
        String sqlString = "Select COLUMN_NAME from INFORMATION_SCHEMA.COLUMNS where TABLE_NAME = ?";
        PreparedStatement preparedStatement = this.connection.prepareStatement(sqlString);
        preparedStatement.setString(1, tableName);
        ResultSet rs = preparedStatement.executeQuery();
        int index = 0;
        while (rs.next()) {
            listColumns.put(rs.getString(1), index);
            index ++;
        }
        return listColumns;
    }

    public Map<String, Column> getMapColumns(String tableName) throws SQLException {
        Map<String, Column> listOutputColumns = new HashMap<>();
        String sqlString = "Select COLUMN_NAME, DATA_TYPE, CHARACTER_MAXIMUM_LENGTH " +
                "from INFORMATION_SCHEMA.COLUMNS where TABLE_NAME = ?";
        PreparedStatement preparedStatement = this.connection.prepareStatement(sqlString);
        preparedStatement.setString(1, tableName);
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
                e.printStackTrace();
                System.out.println("Error when cast to int");
            }
            column.setLength(columnLength);
            listOutputColumns.put(column.getName(), column);
        }

        return listOutputColumns;
    }
}
