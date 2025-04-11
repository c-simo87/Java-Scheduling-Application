package View;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

public class MainMenuController implements Initializable {
@FXML  Button customersButton;
@FXML Button appointmentRecordButton;
@FXML Button appointmentReportButton;
@FXML Button contactReportButton;
@FXML Button customerReportButton;

public void loadScene(String window) throws IOException {

    Parent parent = (Parent) FXMLLoader.load(this.getClass().getResource(window + ".fxml"));;
    Scene scene = new Scene(parent);
    Stage stage = new Stage();
    stage.setScene(scene);
    stage.show();
}


public void onCustomerButton() throws IOException {
loadScene("CustomerRecordView");
}

public void onAppointmentRecordButton() throws IOException {
    loadScene("AppointmentMainView");
}
public void onAppointmentReportButton() throws IOException {
    loadScene("AppointmentReportView");
}

public void onContactReportButton() throws IOException {
    loadScene("ContactReportView");
}

public void onCustomerReportButton() throws IOException {
    loadScene("CustomerReportView");
}

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {

    }
}
