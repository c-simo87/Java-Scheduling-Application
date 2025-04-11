package View;

import Model.Appointment;
import Model.Contact;
import Model.Customer;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.Stage;
import org.w3c.dom.Text;
import util.DBConnection;

import java.io.IOException;
import java.net.URL;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.*;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.util.Optional;
import java.util.ResourceBundle;

public class AppointmentModifyController implements Initializable {
    @FXML
    public TextField titleField;
    @FXML public TextField userIdField;
    @FXML public TextField apptField;
    @FXML public TextField descriptionField;
    @FXML public TextField locationField;
    @FXML public TextField typeField;
    @FXML public DatePicker startDate;
    @FXML public DatePicker endDate;
    @FXML public ComboBox<String> hoursStartCombo;
    @FXML public ComboBox<String> minutesStartCombo;
    @FXML public ComboBox<String> hoursEndCombo;
    @FXML public ComboBox<String> minutesEndCombo;
    @FXML public ComboBox<String> customerCombo;
    @FXML public ComboBox<String> contactCombo;
    @FXML public Button saveButton;
    @FXML public Button cancelButton;

    //private final DateTimeFormatter dtDTF = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
    private final DateTimeFormatter dtDTF = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm");
    private String message = "";
    private boolean outsideBusinessHours = false;
    private boolean overlapAppointment = false;
    //Appointment appt = new Appointment();

    ObservableList<String> hours = FXCollections.observableArrayList();
    ObservableList<String> minutes = FXCollections.observableArrayList();
    ObservableList<String> customerList = FXCollections.observableArrayList();
    ObservableList<String> contactList = FXCollections.observableArrayList();

    AppointmentMainController apptController = new AppointmentMainController();
    Appointment modifiedAppointment = apptController.getModifiedAppointment();


    /**
     * When save button is pressed, check form for any errors and save modified values
     * @param e
     * @throws IOException
     * @throws SQLException
     */
    public void onSavePressed(ActionEvent e) throws IOException, SQLException {
        try {
            String title = titleField.getText();
            String description = descriptionField.getText();
            String location = locationField.getText();
            String type = typeField.getText();
            LocalDate dateStart = startDate.getValue();
            LocalDate dateEnd = endDate.getValue();
            String beginHour = hoursStartCombo.getValue();
            String endHour = hoursEndCombo.getValue();
            String beginMinute = minutesStartCombo.getValue();
            String endMinute = minutesEndCombo.getValue();
            String user = UserLoginController.user.getUserName();

            Timestamp updateTime = new Timestamp(new Date().getTime());
            LocalDateTime localDateTimeBegin = LocalDateTime.of(dateStart.getYear(), dateStart.getMonthValue(), dateStart.getDayOfMonth(), Integer.parseInt(beginHour), Integer.parseInt(beginMinute));
            LocalDateTime localDateTimeEnd = LocalDateTime.of(dateEnd.getYear(), dateEnd.getMonthValue(), dateEnd.getDayOfMonth(), Integer.parseInt(endHour), Integer.parseInt(endMinute));
            ZonedDateTime zonedDateTimeBegin = ZonedDateTime.of(localDateTimeBegin, ZoneId.systemDefault());
            ZonedDateTime zonedDateTimeEnd = ZonedDateTime.of(localDateTimeEnd, ZoneId.systemDefault());
            // obtain the UTC ZonedDateTime of the ZonedDateTime version of LocalDateTime
            ZonedDateTime utcBegin = zonedDateTimeBegin.withZoneSameInstant(ZoneOffset.UTC);
            ZonedDateTime utcEnd = zonedDateTimeEnd.withZoneSameInstant(ZoneOffset.UTC);
            Timestamp startTimeStamp = Timestamp.valueOf(localDateTimeBegin);
            Timestamp endTimeStamp = Timestamp.valueOf(localDateTimeEnd);

            int contactID = contactCombo.getSelectionModel().getSelectedIndex() + 1;
            //int userId = UserLoginController.user.getUserId();
            int customerID = Integer.parseInt(customerCombo.getSelectionModel().getSelectedItem().substring(0, customerCombo.getSelectionModel().getSelectedItem().indexOf(" ")));

            //Check for business hours
            outsideBusinessHours = checkBusinessHours(localDateTimeBegin, localDateTimeBegin, dateStart);

            //Check for overlap
            overlapAppointment = checkCustomerOverlap(localDateTimeBegin, localDateTimeEnd, dateStart);


            if (noErrors()) {
                modifiedAppointment.modifyAppointment(modifiedAppointment.getAppointmentID(),title, description, location, type, startTimeStamp, endTimeStamp, updateTime, updateTime, user, customerID,  contactID);
                Parent parent = (Parent) FXMLLoader.load(this.getClass().getResource("AppointmentMainView.fxml"));
                Scene scene = new Scene(parent);
                Stage stage = new Stage();
                stage.setScene(scene);
                stage.show();
                Stage window = (Stage) ((Node) e.getSource()).getScene().getWindow();
                window.close();
            } else {
                Alert alert = new Alert(Alert.AlertType.WARNING);
                alert.setTitle("Warning Dialog");
                alert.setHeaderText("Invalid Entries");
                alert.setContentText(message);
                alert.show();
            }
        }catch (Exception ex) {
            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.setTitle("Warning Dialog");
            alert.setHeaderText("Invalid Entries");
            alert.setContentText("There was an error with your form. \nPlease make sure dates and times are filled in");
            alert.show();
            ex.printStackTrace();
        }
    }


    /**
     * Check for any errors and display message accordingly
     * @return
     */
    public boolean noErrors()
    {
        message = "";
        //Check for blank entries
        if(titleField.getText().isEmpty())
            message += "Please enter a Title\n";
        if(descriptionField.getText().isEmpty())
            message += "Please enter a Description\n";
        if(typeField.getText().isEmpty())
            message += "Please enter a Type\n";
        if(locationField.getText().isEmpty())
            message += "Please enter a Location\n";
        if(endDate.getValue() == null)
            message += "Please select end date\n";
        if(hoursStartCombo.getSelectionModel() == null)
            message += "Please select an starting hour\n";
        if(hoursEndCombo.getSelectionModel() == null)
            message += "Please select an ending hour\n";
        if(minutesStartCombo.getSelectionModel() == null)
            message += "Please select a starting minute\n";
        if(minutesEndCombo.getSelectionModel() == null)
            message += "Please select a ending minute\n";

        //Check valid date and times
        if(startDate.getValue() != null && endDate.getValue() != null && startDate.getValue().isAfter(endDate.getValue()))
            message += "Start date can not be greater than End date\n";
        if(startDate.getValue() == null || startDate.getValue().isBefore(LocalDate.now()))
            message += "Please select a valid start date\n";
        if((Integer.parseInt(hoursStartCombo.getSelectionModel().getSelectedItem()) > Integer.parseInt(hoursEndCombo.getSelectionModel().getSelectedItem()) )
                && startDate.getValue().equals(endDate.getValue()))
            message += "Starting time can not be greater than ending time\n";
        if((Integer.parseInt(hoursStartCombo.getSelectionModel().getSelectedItem()) == Integer.parseInt(hoursEndCombo.getSelectionModel().getSelectedItem())) &&
                Integer.parseInt(minutesStartCombo.getSelectionModel().getSelectedItem()) > Integer.parseInt(minutesEndCombo.getSelectionModel().getSelectedItem()))
            message += "Starting time can not be greater than ending time\n";


        //Check business hours
        if(outsideBusinessHours)
            message += "You must schedule inside business hours 08:00 AM - 10:00PM";

        //Check overlap
        if(overlapAppointment)
            message += "There is already an appointment scheduled during this time";

        if(message.length() > 0)
            return false;
        return true;

    }

    /**
     * Checks the data base for any overlapping time frames
     * @param startTime
     * @param endTime
     * @param date
     * @return
     */
    public Boolean checkCustomerOverlap(LocalDateTime startTime, LocalDateTime endTime, LocalDate date)
    {
        ObservableList<Appointment> appointmentList = apptController.getApptList();
        //Loop through the appointment list from the database and check for time conflicts
        for(Appointment checkAppointment : appointmentList)
        {
            //parse the string times to local date time
            LocalDateTime checkStartTime = LocalDateTime.parse(checkAppointment.getStart(), dtDTF);
            LocalDateTime checkEndTime = LocalDateTime.parse(checkAppointment.getEnd(),dtDTF);

            // Check for overlap between the conflict times
            if( checkStartTime.isBefore(startTime) && checkEndTime.isAfter(endTime)) {
                return true;
            }
            // Check if the time starts during the scheduled time
            if (checkStartTime.isBefore(endTime) & checkStartTime.isAfter(startTime)) {
                return true;
            }
            // ConflictAppt end time falls anywhere in the new appt
            if (checkEndTime.isBefore(endTime) & checkEndTime.isAfter(startTime)) {
                return true;
            }

        }
        return false;
    }

    /**
     * Checks the selected time if its outside of business hours in New York timezone
     * @param startTime
     * @param endTime
     * @param date
     * @return
     */
    //check if the hours are outside of business hours
    public Boolean checkBusinessHours(LocalDateTime startTime, LocalDateTime endTime, LocalDate date)
    {
        ZonedDateTime zoneStart = ZonedDateTime.of(startTime, UserLoginController.user.getZoneId());
        ZonedDateTime zoneEnd = ZonedDateTime.of(endTime, UserLoginController.user.getZoneId());

        ZonedDateTime businessStart = ZonedDateTime.of(date, LocalTime.of(8,0),
                ZoneId.of("America/New_York"));
        ZonedDateTime businessEnd = ZonedDateTime.of(date, LocalTime.of(22, 0),
                ZoneId.of("America/New_York"));
        if(zoneStart.isBefore(businessStart) || zoneStart.isAfter(businessEnd) || zoneEnd.isBefore(businessStart) || zoneEnd.isAfter(businessEnd))
            return true;
        return false;
    }

    /**
     * Set customer combo values
     */
    public void setCustomerCombo()
    {
        String fullContact = "";
        try{
            String sqlStatement = "SELECT customer_id, customer_name from customers";
            Statement statement = DBConnection.startConnection().createStatement();
            ResultSet rs = statement.executeQuery(sqlStatement);
            while(rs.next())
            {
                Customer customer  = new Customer();
                customer.setCustomerId(rs.getInt("customer_id"));
                customer.setCustomerName(rs.getString("customer_name"));
                fullContact = Integer.toString(customer.getCustomerId()) + " " + customer.getCustomerName();
                customerList.add(fullContact);
            }
        }catch (Exception e) {e.printStackTrace();}
        customerCombo.setItems(customerList);
    }

    /**
     * Set the contact combo values
     */
    public void setContactCombo() {
        String fullContact = "";
        try {
            String sqlStatement = "SELECT contact_id, contact_Name from contacts";
            Statement statement = DBConnection.startConnection().createStatement();
            ResultSet rs = statement.executeQuery(sqlStatement);
            while (rs.next()) {
                Contact contact = new Contact();
                contact.setContactId(rs.getInt("contact_ID"));
                contact.setContactName(rs.getString("contact_name"));
                fullContact = Integer.toString(contact.getContactId()) + " " + contact.getContactName();
                contactList.add(fullContact);
            }
        }catch (Exception e) {e.printStackTrace();}
        contactCombo.setItems(contactList);

    }

    /**
     * Cancel button is pressed. Warn for data loss
     * @param e
     * @throws IOException
     */
    public void onCancelPressed(ActionEvent e) throws IOException {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Warning Dialog");
        alert.setHeaderText("Data is unsaved, are you sure you would like to cancel?");
        Optional<ButtonType> result = alert.showAndWait();
        if(result.get() == ButtonType.OK) {
            Parent parent = (Parent) FXMLLoader.load(this.getClass().getResource("AppointmentMainView.fxml"));
            Scene scene = new Scene(parent);
            Stage stage = new Stage();
            stage.setScene(scene);
            Stage window = (Stage) ((Node) e.getSource()).getScene().getWindow();
            window.close();
        }
    }


    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        hours.addAll("00", "01", "02", "03", "04", "05", "06", "07", "08", "09", "10", "11",
                "12", "13", "14", "15", "16", "17", "18", "19", "20", "21", "22", "23");
        minutes.addAll("00", "15", "30", "45");
        hoursStartCombo.setItems(hours);
        hoursEndCombo.setItems(hours);
        minutesStartCombo.setItems(minutes);
        minutesEndCombo.setItems(minutes);

        setCustomerCombo();
        setContactCombo();


        try {
            //DatePicker
            startDate.setValue(LocalDate.parse(modifiedAppointment.getStart().substring(0, 10)));
            endDate.setValue(LocalDate.parse(modifiedAppointment.getEnd().substring(0, 10)));
            hoursStartCombo.setValue(modifiedAppointment.getStart().substring(11, 13));
            hoursEndCombo.setValue(modifiedAppointment.getEnd().substring(11,13));
            minutesStartCombo.setValue(modifiedAppointment.getStart().substring(14,16));
            minutesEndCombo.setValue(modifiedAppointment.getEnd().substring(14,16));

            //fields
            titleField.setText(modifiedAppointment.getTitle());
            descriptionField.setText(modifiedAppointment.getDescription());
            typeField.setText(modifiedAppointment.getType());
            locationField.setText(modifiedAppointment.getLocation());
            userIdField.setText(String.valueOf(modifiedAppointment.getUserID()));
            apptField.setText(String.valueOf(modifiedAppointment.getAppointmentID()));

            //Customer,Contact Combo
            int customerId = modifiedAppointment.getCustomerID();
            int contactId = modifiedAppointment.getContactID();
            customerCombo.getSelectionModel().select(customerId-1);
            contactCombo.getSelectionModel().select(contactId-1);

        }catch(Exception e){e.printStackTrace();}
    }

}
