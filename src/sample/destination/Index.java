package sample.destination;

import com.connection.ExcelConnection;
import com.connection.SqlServerConnection;
import com.dataflow.DataFlow;
import com.model.Excel;
import com.model.SqlServer;
import com.services.SqlServerService;
import com.xml.XmlHelper;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.Pane;
import javafx.stage.FileChooser;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;

public class Index implements Initializable {
    @FXML
    private Label hideLabel;
    @FXML
    private TextField txtFilePath;
    @FXML
    private ComboBox cbListSheetName;
    @FXML
    private Pane buttonPane;
    @FXML
    private TextField ssHostnameTxt;
    @FXML
    private TextField ssUsernameTxt;
    @FXML
    private PasswordField ssPasswordTxt;
    @FXML
    private TextField ssDatabaseTxt;
    @FXML
    private ComboBox ssTableCb;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        int sheetIndex = cbListSheetName.getSelectionModel().getSelectedIndex();
        if (sheetIndex == -1) {
            buttonPane.setVisible(false);
        }
    }
    public void closeStage() {
        Stage stage = (Stage) hideLabel.getScene().getWindow();
        stage.close();
    }
    /**
     * Excel section
     */
    @FXML
    private void selectExcelFile() {
        buttonPane.setVisible(false);
        FileChooser fileChooser = new FileChooser();
        FileChooser.ExtensionFilter extensionFilter = new FileChooser.ExtensionFilter("Excel (*.xlsx)",
                "*.xlsx");
        fileChooser.getExtensionFilters().add(extensionFilter);
        File file = fileChooser.showOpenDialog(null);
        if (file != null) {
            String filePath = file.getPath();
            txtFilePath.setText(filePath);
            showComboSheet();
        }
    }
    @FXML
    private void showComboSheet() {
        String filePath = txtFilePath.getText();
        ExcelConnection excelConnection = new ExcelConnection(filePath);
        try {
            cbListSheetName.getItems().clear();
            ArrayList<String> listSheetName = excelConnection.getListSheet();
            listSheetName.forEach(item -> cbListSheetName.getItems().add(item));
            showButtonPane();
        } catch (IOException e) {
            e.printStackTrace();
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setContentText("File not found!");
            alert.show();
        }
    }

    @FXML
    private void showButtonPane() {
        buttonPane.setVisible(true);
    }

    @FXML
    private void showNewSheet() throws IOException {
        Excel excel = new Excel(txtFilePath.getText());
        XmlHelper.excelTemp2Xml(excel);

        Parent parent = FXMLLoader.load(getClass().getResource("new_sheet.fxml"));
        Stage stage = new Stage();
        stage.setTitle("New Sheet");
        stage.setScene(new Scene(parent));
        stage.initModality(Modality.APPLICATION_MODAL);
        stage.showAndWait();
    }

    @FXML
    private void showExcelColumnSelect() throws IOException {
        Excel excel = new Excel(txtFilePath.getText());
        int sheetIndex = cbListSheetName.getSelectionModel().getSelectedIndex();
        if (sheetIndex == -1) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setContentText("Please select one sheet!");
            alert.show();
            return;
        }
        excel.setSheetIndex(sheetIndex);
        XmlHelper.excelTemp2Xml(excel);
        Parent parent = FXMLLoader.load(getClass().getResource("excel_column_config.fxml"));
        Stage stage = new Stage();
        stage.setTitle("Select columns to load");
        stage.setScene(new Scene(parent));
        stage.initModality(Modality.APPLICATION_MODAL);
        stage.showAndWait();
        closeStage();
    }

    /**
     * Sql Server Section
     *
     * */
    private boolean sqlServerValidate() {
        String hostName = ssHostnameTxt.getText();
        String userName = ssUsernameTxt.getText();
        String password = ssPasswordTxt.getText();
        String database = ssDatabaseTxt.getText();

        if (hostName.trim().isEmpty() || userName.trim().isEmpty() ||
                password.trim().isEmpty() || database.trim().isEmpty()) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setContentText("Please fill full information");
            alert.show();
            return false;
        }
        return true;
    }

    private Connection testSSConnection() {
        String userName = ssUsernameTxt.getText().trim();
        String hostName = ssHostnameTxt.getText().trim();
        String password = ssPasswordTxt.getText().trim();
        String database = ssDatabaseTxt.getText().trim();
        SqlServerConnection sqlServerConnection = new SqlServerConnection(hostName, userName, password);
        sqlServerConnection.setDatabaseName(database);
        Connection conn = null;
        try {
            conn = sqlServerConnection.getConnection();
        } catch (ClassNotFoundException e) {
            System.out.println("Class not Found");
        } catch (SQLException e) {
            System.out.println("Ket noi khong thanh cong");
        }
        return conn;
    }

    private List<String> ssGetListTable() throws SQLException {
        Connection conn = testSSConnection();
        if (conn == null) {
            ssTableCb.getItems().clear();
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setContentText("Connection failed!");
            alert.show();
            return null;
        }
        SqlServerService sqlServerService = new SqlServerService(conn);
        List<String> tableNames = sqlServerService.getListTableNames();
        return tableNames;
    }

    @FXML
    private void ssShowTableNames() throws SQLException {
        if (!sqlServerValidate()) {
            ssTableCb.getItems().clear();
            return;
        }
        ssTableCb.getItems().clear();
        List<String> listTableNames = ssGetListTable();
        listTableNames.forEach(name -> ssTableCb.getItems().add(name));
        ssTableCb.show();
    }

    @FXML
    private void ssNewTable() throws IOException {
        Parent parent = FXMLLoader.load(getClass().getResource("new_table.fxml"));
        Stage stage = new Stage();
        stage.setTitle("New Table");
        stage.setScene(new Scene(parent));
        stage.initModality(Modality.APPLICATION_MODAL);
        stage.showAndWait();
    }

    @FXML
    private void ssShowMappings() throws SQLException {
        if (!sqlServerValidate()) {
            ssTableCb.getItems().clear();
            return;
        }

        if (ssGetListTable() == null) {
            return;
        }

        if (ssTableCb.getSelectionModel().getSelectedIndex() == -1) {
            Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
            alert.setContentText("Please select table name");
            alert.show();
            return;
        }
        String tableName = (String) ssTableCb.getSelectionModel().getSelectedItem();
        ssSaveTempConfig(tableName);
        try {
            ssShowMappingConfig();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void ssSaveTempConfig(String tableName) {
        String userName = ssUsernameTxt.getText().trim();
        String hostName = ssHostnameTxt.getText().trim();
        String password = ssPasswordTxt.getText().trim();
        String database = ssDatabaseTxt.getText().trim();

        SqlServer sqlServer = new SqlServer(hostName, userName, password, database);
        sqlServer.setTableName(tableName);
        XmlHelper.SqlServerToXml(sqlServer);
    }

    private void ssShowMappingConfig() throws IOException {
        closeStage();
        Parent parent = FXMLLoader.load(getClass().getResource("ss_mapping_config.fxml"));
        Stage stage = new Stage();
        stage.setTitle("Mappings");
        stage.setScene(new Scene(parent));
        stage.initModality(Modality.APPLICATION_MODAL);
        stage.showAndWait();
    }
}
