package sample.transform;

import com.dataflow.DataFlow;
import com.dataflow.components.SourceInterface;
import com.model.Column;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.Modality;
import javafx.stage.Stage;
import sample.Session;

import java.io.IOException;
import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;

public class Index implements Initializable {
    @FXML
    private ListView listView;
    @FXML
    private TextField txtName;
    @FXML
    private TextField txtSize;
    @FXML
    private TextArea txtExpression;
    @FXML
    private ComboBox cbDataType;

    ObservableList observableList = FXCollections.observableArrayList();
    private List<Column> listColumns;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        cbDataType.getItems().add("String");
        cbDataType.getItems().add("int");

        DataFlow dataFlow = Session.getDataFlow();
        SourceInterface source = dataFlow.getExecutables().getPineline().getComponents().getSource();
        List<Column> listCurrentColumns = source.getOutputColumns();
        fillToListView(listCurrentColumns);
    }

    private void fillToListView(List<Column> listColumns) {
        observableList.clear();
        listView.getItems().clear();
        listColumns.forEach(column -> {
            observableList.add(column.getName());
        });
        listView.getItems().addAll(observableList);
        listView.refresh();
    }

    private boolean validate() {
        String name = txtName.getText().trim();
        String length = txtSize.getText().trim();
        String expression = txtExpression.getText().trim();
        Alert alert = new Alert(Alert.AlertType.ERROR);
        if (name.isEmpty() || length.isEmpty() || expression.isEmpty()) {
            alert.setContentText("Please fill full");
            alert.show();
            return false;
        }
        try {
            int columnName = Integer.valueOf(length);
        } catch (Exception e) {
            alert.setContentText("Size must be numeric!");
            alert.show();
            return false;
        }
        return true;
    }

    @FXML
    private void saveColumn() {
        if (!validate()) {
            return;
        }
        System.out.println("Yes");
    }

    @FXML
    private void reset() {
        txtName.setText("");
        txtExpression.setText("");
        txtSize.setText("");
    }

    @FXML
    private void showExpressionTutorial() throws IOException {
        Parent root = FXMLLoader.load(getClass().getResource("expression_tutorial.fxml"));
        Stage stage = new Stage();
        stage.setScene(new Scene(root));
        stage.setTitle("Expression Tutorial");
        stage.initModality(Modality.APPLICATION_MODAL);
        stage.showAndWait();
    }
}
