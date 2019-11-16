package sample.destination;

import com.dataflow.Components;
import com.dataflow.DataFlow;
import com.model.Column;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.TextArea;
import sample.Session;

import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;

public class SsNewTable implements Initializable {
    @FXML
    private TextArea txtNewTable;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        DataFlow dataFlow = Session.getDataFlow();

        List<Column> sourceColumns = new ArrayList<>();

        Components components = dataFlow.getExecutables().getPineline().getComponents();

        components.getSource().getOutputColumns().forEach(column -> {
            sourceColumns.add(column);
        });

        if (components.getDerivedColumn() != null) {
            List<Column> derivedColumns = components.getDerivedColumn().getOutputColumns();
            derivedColumns.forEach(column -> {
                sourceColumns.add(column);
            });
        }

        String columnInSqlString = "";
        for (int i = 0; i < sourceColumns.size(); i++) {
            Column column = sourceColumns.get(i);
            columnInSqlString += column.getName() + " " + "nvarchar(" + column.getLength() +") \n";
        }

        String sqlNewTable = "Create TABLE [TableName] (\n"+ columnInSqlString +")";
        txtNewTable.setText(sqlNewTable);
    }
}
