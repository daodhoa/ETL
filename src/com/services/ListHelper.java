package com.services;

import com.model.Column;

import java.util.List;

public class ListHelper {
    public static Column searchListColumn(List<Column> listColumns, String columnName) {
        Column column = null;
        for (int i = 0; i < listColumns.size(); i++) {
            if (listColumns.get(i).getName().equals(columnName)) {
                column = listColumns.get(i);
                break;
            }
        }
        return column;
    }
}
