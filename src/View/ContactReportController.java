package View;

import Model.Appointment;
import Model.Contact;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import util.DBConnection;

import java.net.URL;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ResourceBundle;

public class ContactReportController implements Initializable {
    @FXML public TableView tableView;
    @FXML public TableColumn<Appointment, Integer> idColumn;
    @FXML public TableColumn<Appointment, String> titleColumn;
    @FXML public TableColumn<Appointment, String> descriptionColumn;
    @FXML public TableColumn<Appointment, String> locationColumn;
    @FXML public TableColumn<Appointment, String> typeColumn;
    @FXML public TableColumn<Appointment, String> startColumn;
    @FXML public TableColumn<Appointment, String> endColumn;
    @FXML public TableColumn<Appointment, Integer> customerIDColumn;
    @FXML public ComboBox<String> contactCombo;

    ObservableList<String> contactComboList = FXCollections.observableArrayList();
    ObservableList<Appointment> appointments = FXCollections.observableArrayList();

    /**
     * Query the database for contacts and populate the view with values
     * @param contactName
     * @throws SQLException
     */
    public void setTableView(String contactName) throws SQLException {
        appointments.clear();
        int contactId = Contact.searchContactID(contactName);
        String sqlStatement = "Select * From appointments Where Contact_ID = \"" + contactId + "\"";
        Statement statement = DBConnection.startConnection().createStatement();
        ResultSet rs = statement.executeQuery(sqlStatement);
        while(rs.next())
        {
            Appointment appt = new Appointment();
            appt.setAppointmentID(rs.getInt("appointment_id"));
            appt.setCustomerID(rs.getInt("Customer_ID"));
            appt.setTitle(rs.getString("title"));
            appt.setDescription("description");
            appt.setType(rs.getString("type"));
            appt.setStart(rs.getString("start"));
            appt.setEnd(rs.getString("end"));
            appointments.add(appt);
        }
        tableView.setItems(appointments);
    }

    /**
     * When a selection is made form the contact combo box
     * @throws SQLException
     */
    public void onComboBoxSelection() throws SQLException {
        String contactName = contactCombo.getSelectionModel().getSelectedItem();
        setTableView(contactName);
    }

    /**
     * Set the combo box values
     */
    public void setContactCombo()
    {
        Contact.getAllContacts(contactComboList);
        contactCombo.setItems(contactComboList);
    }



    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
      this.idColumn.setCellValueFactory(new PropertyValueFactory<Appointment, Integer>("appointmentID"));
        this.titleColumn.setCellValueFactory(new PropertyValueFactory<Appointment,String>("title"));
        this.descriptionColumn.setCellValueFactory(new PropertyValueFactory<Appointment,String>("description"));
        this.typeColumn.setCellValueFactory(new PropertyValueFactory<Appointment,String>("type"));
        this.startColumn.setCellValueFactory(new PropertyValueFactory<Appointment,String>("start"));
        this.endColumn.setCellValueFactory(new PropertyValueFactory<Appointment,String>("end"));
        this.customerIDColumn.setCellValueFactory(new PropertyValueFactory<Appointment,Integer>("customerID"));
        setContactCombo();
    }
}
