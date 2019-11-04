package sample.source;

import com.connection.ExcelConnection;
import com.connection.SqlServerConnection;
import com.dataflow.*;
import com.dataflow.components.SqlServerSource;
import com.model.Column;
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
import sample.Session;

import javax.xml.bind.annotation.XmlElement;
import java.awt.peer.PanelPeer;
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
    private CheckBox checkBox;
    @FXML
    private Pane buttonPane;

    @FXML
    private TextField ssHostnameTxt;
    @FXML
    private TextField ssUsernameTxt;
    @FXML
    private TextField ssDatabaseTxt;
    @FXML
    private PasswordField ssPasswordTxt;

    @FXML
    private ComboBox ssTableCb;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        int sheetIndex = cbListSheetName.getSelectionModel().getSelectedIndex();
        if (sheetIndex == -1) {
            buttonPane.setVisible(false);
        }
        ssTableCb.setVisible(false);
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
        FileChooser fileChooser = new FileChooser();
        FileChooser.ExtensionFilter extensionFilter = new FileChooser.ExtensionFilter("Excel (*.xlsx)",
                "*.xlsx");
        fileChooser.getExtensionFilters().add(extensionFilter);
        File file = fileChooser.showOpenDialog(null);
        if (file != null) {
            String filePath = file.getPath();
            txtFilePath.setText(filePath);
            showComboSheet(filePath);
        }
    }

    private void showComboSheet(String filePath) {
        ExcelConnection excelConnection = new ExcelConnection(filePath);
        try {
            ArrayList<String> listSheetName = excelConnection.getListSheet();
            listSheetName.forEach(item -> cbListSheetName.getItems().add(item));
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
    public void showExcelColumnConfig() throws IOException {
        saveExcelToXml();
        closeStage();
        Parent root = FXMLLoader.load(getClass().getResource("excel_column_config.fxml"));
        Stage stage = new Stage();
        stage.setScene(new Scene(root));
        stage.setTitle("Source Configuration");
        stage.initModality(Modality.WINDOW_MODAL);
        stage.showAndWait();
    }

    private void saveExcelToXml() {
        ConnectionManager connectionManager = new ConnectionManager();
        Excel excel = new Excel();
        excel.setFilePath(txtFilePath.getText());
        int sheetIndex = cbListSheetName.getSelectionModel().getSelectedIndex();
        boolean isFirstRow = checkBox.isSelected();
        excel.setSheetIndex(sheetIndex);
        excel.setFirstRow(isFirstRow);
        ExcelManager excelManager = new ExcelManager(excel);
        connectionManager.setExcelManager(excelManager);
        DataFlow dataFlow = Session.getDataFlow();
        dataFlow.setConnectionManager(null);
        dataFlow.setExecutables(null);
        dataFlow.setConnectionManager(connectionManager);
        XmlHelper.Dataflow2Xml(dataFlow);
    }

    /**
     * Sql server section
     */
    private boolean sqlServerValidate() {
        String hostName = ssHostnameTxt.getText();
        String userName = ssUsernameTxt.getText();
        String password = ssPasswordTxt.getText();
        String database = ssDatabaseTxt.getText();

        String errorContent = "";
        boolean isError = false;

        if (hostName.trim().isEmpty()) {
            isError = true;
            errorContent += "Hostname is required! ";
        }
        if (userName.trim().isEmpty()) {
            isError = true;
            errorContent += "Username is required! ";
        }
        if (password.trim().isEmpty()) {
            isError = true;
            errorContent += "Password is required! ";
        }
        if (database.trim().isEmpty()) {
            isError = true;
            errorContent += "Database is required";
        }

        if (isError) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setContentText(errorContent);
            alert.show();
        }
        return isError;
    }

    private Connection testSSConnection() {
        String hostName = ssHostnameTxt.getText().trim();
        String userName = ssUsernameTxt.getText().trim();
        String password = ssPasswordTxt.getText().trim();
        String database = ssDatabaseTxt.getText().trim();
        SqlServerConnection sqlServerConnection = new SqlServerConnection(hostName, userName, password);
        sqlServerConnection.setDatabaseName(database);
        Connection conn = null;
        try {
            conn = sqlServerConnection.getConnection();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return conn;
    }

    private List<String> ssGetListTable() throws SQLException {
        Connection conn = testSSConnection();
        if (conn == null) {
            ssTableCb.setVisible(false);
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
    private void ssShowTableNameSelection() throws SQLException {
        if (sqlServerValidate()) {
            ssTableCb.setVisible(false);
            return;
        }

        List<String> listTableNames = ssGetListTable();
        listTableNames.forEach(name -> ssTableCb.getItems().add(name));
        ssTableCb.setVisible(true);
        ssTableCb.show();
    }

    @FXML
    private void ssHandleOk() throws SQLException {
        if (sqlServerValidate()) {
            ssTableCb.setVisible(false);
            return;
        }

        List<String> listTableNames = ssGetListTable();
        if (listTableNames == null) {
            return;
        }

        int tableIndex = ssTableCb.getSelectionModel().getSelectedIndex();
        if (tableIndex == -1) {
            Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
            alert.setContentText("Please select table name");
            alert.show();
        }
        String tableName = (String) ssTableCb.getSelectionModel().getSelectedItem();
        System.out.println(tableName);
        if(!listTableNames.contains(tableName)) {
            return;
        }
        try {
            ssSaveSqlServerConfig(tableName);
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
        closeStage();
    }

    private void ssSaveSqlServerConfig(String tableName) throws SQLException, ClassNotFoundException {
        String hostName = ssHostnameTxt.getText().trim();
        String userName = ssUsernameTxt.getText().trim();
        String password = ssPasswordTxt.getText().trim();
        String database = ssDatabaseTxt.getText().trim();

        DataFlow dataFlow = Session.getDataFlow();
        dataFlow.refresh();

        ConnectionManager connectionManager = new ConnectionManager();
        SqlServer sqlServer = new SqlServer(hostName, userName, password, database);
        SqlServerManager sqlServerManager = new SqlServerManager(sqlServer);
        connectionManager.setSqlServerManager(sqlServerManager);
        dataFlow.setConnectionManager(connectionManager);

        SqlServerSource sqlServerSource = new SqlServerSource();
        sqlServerSource.setTableName(tableName);
        sqlServerSource.setConnectionManagerRefId(sqlServerManager.getRefId());

        SqlServerConnection sqlServerConnection = new SqlServerConnection(hostName, userName, password);
        sqlServerConnection.setDatabaseName(database);
        Connection conn = sqlServerConnection.getConnection();
        SqlServerService sqlServerService = new SqlServerService(conn);
        List<Column> outputColumns = sqlServerService.getListOutputColumns(tableName);
        outputColumns.forEach(column -> {
            column.setId("DataFlow.SqlServerSource.Outputs[SqlServerSourceOutput].Columns["+ column.getName() +"]");
        });
        sqlServerSource.setOutputColumns(outputColumns);

        Components components = new Components();
        components.setSqlServerSource(sqlServerSource);
        Pineline pineline = new Pineline();
        pineline.setComponents(components);
        Executables executables = new Executables();
        executables.setPineline(pineline);
        dataFlow.setExecutables(executables);

        XmlHelper.Dataflow2Xml(dataFlow);
    }
}
