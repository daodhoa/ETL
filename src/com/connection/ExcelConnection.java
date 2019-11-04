package com.connection;

import com.model.Column;
import org.apache.poi.ss.usermodel.*;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class ExcelConnection {
    private String filePath;

    public ExcelConnection(){};

    public ExcelConnection(String filePath) {
        this.filePath = filePath;
    }

    public String getFilePath() {
        return filePath;
    }

    public void setFilePath(String filePath) {
        this.filePath = filePath;
    }

    private Workbook readExcel() throws IOException {
        File file = new File(getFilePath());
        Workbook workbook = WorkbookFactory.create(file);
        workbook.close();
        return workbook;
    }

    public ArrayList<String> getListSheet() throws IOException {
        ArrayList<String> listSheet = new ArrayList<>();
        Workbook workbook = this.readExcel();
        Iterator<Sheet> sheetIterator = workbook.sheetIterator();
        while (sheetIterator.hasNext()) {
            Sheet sheet = sheetIterator.next();
            listSheet.add(sheet.getSheetName());
        }
        return listSheet;
    }

    public Sheet getSheetAtIndex(int index) throws IOException {
        Workbook workbook = this.readExcel();
        return workbook.getSheetAt(index);
    }

    public List<Column> getColumn(int index, boolean isFirstRow) throws IOException {
        List<Column> listColumn = new ArrayList<>();
        Sheet sheet = this.getSheetAtIndex(index);
        Row firstRow = sheet.getRow(0);
        if (isFirstRow == false) {
            int numberOfColumn = firstRow.getLastCellNum();
            for (int i = 0; i < numberOfColumn; i++) {
                Column column = new Column("Column " + i, "String", 100);
                listColumn.add(column);
            }
            return listColumn;
        }
        DataFormatter dataFormatter = new DataFormatter();
        Iterator<Cell> cellIterator = firstRow.cellIterator();
        while (cellIterator.hasNext()) {
            Cell cell = cellIterator.next();
            Column column = new Column(dataFormatter.formatCellValue(cell), "String", 100);
            listColumn.add(column);
        }

        return listColumn;
    }

    public List<List<String>> getDataFromSheet(int index) throws IOException {
        Sheet sheet = this.getSheetAtIndex(index);
        List<List<String>> listData = new ArrayList<>();
        DataFormatter dataFormatter = new DataFormatter();
        Iterator<Row> rowIterator = sheet.rowIterator();
        while (rowIterator.hasNext()) {
            Row row = rowIterator.next();
            List<String> listCellData = new ArrayList<>();
            Iterator<Cell> cellIterator = row.cellIterator();
            while (cellIterator.hasNext()) {
                Cell cell = cellIterator.next();
                String cellValue = dataFormatter.formatCellValue(cell);
                listCellData.add(cellValue);
            }
            listData.add(listCellData);
        }
        return listData;
    }
}
