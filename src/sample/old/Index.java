package sample.old;

import com.dataflow.ConnectionManager;
import com.dataflow.DataFlow;
import com.dataflow.SqlServerManager;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import sample.Session;

import java.net.URL;
import java.util.ResourceBundle;

public class Index implements Initializable {
    @FXML
    private Label lblSourceName;
    @FXML
    private Label lblDestName;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        /*DataFlow dataFlow = Session.getDataFlow();
        SqlServerManager sqlServerManager = dataFlow.getConnectionManager().getSqlServerManager();*/

        /*lblSourceName.setText(sqlServerManager.getRefId());
        ConnectionManager connectionManager = dataFlow.getConnectionManager();
        lblDestName.setText(connectionManager.getRefIdOfDestination());*/
    }
}
