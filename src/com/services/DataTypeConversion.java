package com.services;

import com.enums.DataType;

public class DataTypeConversion {
    public static DataType database2Java(String type) {
        DataType out;
        switch (type) {
            case "int":
            case "tinyint":
            case "smallint":
                out = DataType.INT;
                break;
            case "varchar":
            case "nvarchar":
            case "char":
            case "nchar":
                out = DataType.STRING;
                break;
            case "real":
            case "float":
                out = DataType.FLOAT;
                break;
            case "bit":
                out = DataType.BOOLEAN;
                break;
            case "date":
                out = DataType.DATE;
                break;
            case "time":
                out = DataType.TIME;
                break;
            case "timestamp":
                out = DataType.TIMESTAMP;
                break;
            default:
                out = DataType.NOTFOUND;
                break;
        }
        return out;
    }
}
