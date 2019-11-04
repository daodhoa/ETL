package com.services;

        import com.connection.ExcelConnection;
        import com.model.Column;
        import com.model.Excel;

        import java.io.IOException;
        import java.sql.Connection;
        import java.sql.PreparedStatement;
        import java.sql.ResultSet;
        import java.sql.SQLException;
        import java.util.ArrayList;
        import java.util.HashMap;
        import java.util.List;
        import java.util.Map;

public class SqlServerService {
    private Connection connection;
    public SqlServerService(Connection connection) {
        this.connection = connection;
    }

    public Connection getConnection() {
        return connection;
    }

    public void setConnection(Connection connection) {
        this.connection = connection;
    }

    public List<String> getListTableNames() throws SQLException {
        List<String> listTableNames = new ArrayList<>();
        String sqlString = "SELECT TABLE_NAME FROM INFORMATION_SCHEMA.TABLES WHERE TABLE_TYPE = 'BASE TABLE'";
        PreparedStatement preparedStatement = this.connection.prepareStatement(sqlString);
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
                e.printStackTrace();
                System.out.println("Error when cast to int");
            }
            column.setLength(columnLength);
            listOutputColumns.add(column);
        }

        return listOutputColumns;
    }

    public void execute(String tableName, Map<String, Integer> mappings, Excel excel) throws IOException, SQLException {
        ExcelConnection excelConnection = new ExcelConnection(excel.getFilePath());
        List<List<String>> listData = excelConnection.getDataFromSheet(excel.getSheetIndex());
        boolean isFirstRow = excel.isFirstRow();
        int i = 0;
        if (isFirstRow) i = 1;

        String columns = "";
        String values = "";
        for (Map.Entry<String, Integer> entry: mappings.entrySet()) {
            columns = columns + entry.getKey() + ", ";
            values = values + "?, ";
        }

        columns = columns.substring(0, columns.length() - 2);
        values = values.substring(0, values.length() - 2);

        for ( ; i < listData.size(); i++) {
            List<String> listCellData = listData.get(i);
            String sql = "INSERT INTO " + tableName + " ( " + columns + " ) VALUES (" + values +")" ;
            PreparedStatement preparedStatement = this.connection.prepareStatement(sql);
            int j = 1;
            for (Map.Entry<String, Integer> entry: mappings.entrySet()) {
                preparedStatement.setString(j, listCellData.get(entry.getValue()));
                j++;
            }
            int row = preparedStatement.executeUpdate();
            System.out.println(row);
        }

    }
}
