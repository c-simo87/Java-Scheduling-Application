package View;

import View.UserLoginController;
import Model.Appointment;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;
import util.DBConnection;

import javax.swing.*;
import java.io.IOException;
import java.net.URL;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Optional;
import java.util.ResourceBundle;

public class AppointmentMainController implements Initializable {

@FXML public TableView view;
@FXML public TableColumn<Appointment, Integer> idColumn;
@FXML public TableColumn<Appointment, String> titleColumn;
@FXML public TableColumn<Appointment, String> descriptionColumn;
@FXML public TableColumn<Appointment, String> locationColumn;
@FXML public TableColumn<Appointment, String> typeColumn;
@FXML public TableColumn<Appointment, String> startColumn;
@FXML public TableColumn<Appointment, String> endColumn;
@FXML public TableColumn<Appointment, Integer> customerIDColumn;
@FXML public TableColumn<Appointment, Integer> userIDColumn;
@FXML public TableColumn<Appointment, Integer> contactIDColumn;
@FXML public Button addApptButton;
@FXML public Button modifyApptButton;
@FXML public Button deleteApptButton;
@FXML public RadioButton noFilterButton;
@FXML public RadioButton monthRadio;
@FXML public RadioButton weekRadio;

    public static ObservableList<Appointment> apptList = FXCollections.observableArrayList();
    ToggleGroup group = new ToggleGroup();

    //Time format vars
    private final DateTimeFormatter dtDTF = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm");

    public static Appointment modifiedAppointment = new Appointment();

    /**
     * Query the database for appointments and populate table view
     * @throws SQLException
     */
    public void populateAppointmentTable() throws SQLException{
        apptList.clear();
        try{
            String sqlStatement = "select appointment_id,title,description,location,type,start,end,appointments.customer_id,appointments.contact_id,appointments.user_id "
            + "from appointments,customers,contacts,users where appointments.Contact_id = contacts.Contact_id "
            + "and appointments.user_id = users.user_id and appointments.customer_id = customers.customer_id ORDER BY appointment_id";

            Statement statement = DBConnection.startConnection().createStatement();
            ResultSet rs = statement.executeQuery(sqlStatement);

            while(rs.next())
            {
                Appointment appt = new Appointment();
                appt.setAppointmentID(rs.getInt("appointment_id"));
                appt.setCustomerID(rs.getInt("Customer_ID"));
                appt.setContactID(rs.getInt("contact_ID"));
                appt.setUserID(rs.getInt("user_id"));
                appt.setTitle(rs.getString("title"));
                appt.setDescription(rs.getString("description"));
                appt.setLocation(rs.getString("location"));
                appt.setType(rs.getString("type"));

                /*Extracting time stamp data from UTC in data base from Start & End
                * Converting the data to the users Local Zone Date Time */
                /*Timestamp tsStart = rs.getTimestamp("start");
                Timestamp toEnd = rs.getTimestamp("end");
                ZoneId zoneID = ZoneId.systemDefault();
                ZonedDateTime zDTStart = tsStart.toLocalDateTime().atZone(ZoneId.of("UTC"));
                ZonedDateTime zDTEnd = toEnd.toLocalDateTime().atZone(ZoneId.of("UTC"));
                ZonedDateTime zLocalStart = zDTStart.withZoneSameInstant(zoneID);
                ZonedDateTime zLocalEnd = zDTEnd.withZoneSameInstant(zoneID);
                appt.setStart(zLocalStart.toString());
                appt.setEnd(zLocalEnd.toString());*/

                //TODO

                //Grab the UTC time stamps from the data base
                //String startUTC = rs.getString("start").substring(0, 19);
                //String endUTC = rs.getString("end").substring(0, 19);
                //System.out.println("Database time: " + startUTC);

                //Convert from UTC to local date and time
                LocalDateTime localStartDT = rs.getTimestamp("start").toLocalDateTime();
                LocalDateTime localEndDT = rs.getTimestamp("end").toLocalDateTime();
                //System.out.println("UTC to localdatetime: " + localStartDT);

                //Set zone id
                //ZoneId localZoneID = ZoneId.systemDefault();
                //ZoneId localZoneID = UserLoginController.user.getZoneId();
                //ZoneId utcZoneID = ZoneId.of("UTC");

                //ZonedDateTime localZoneStart = utcStart.atZone(utcZoneID).withZoneSameInstant(localZoneID);
                //ZonedDateTime localZoneEnd = utcEnd.atZone(utcZoneID).withZoneSameInstant(localZoneID);
                //System.out.println("To Zone date time: " + localZoneStart);

                //ZonedDateTime localZoneStart = utcStart.atZone(localZoneID);
                //ZonedDateTime localZoneEnd = utcEnd.atZone(localZoneID);

                //Convert the zone times to be added to the Appointment List
                //String localStartDT = localZoneStart.format(dtDTF);
                //String localEndDT = localZoneEnd.format(dtDTF);
                appt.setStart(localStartDT.toString());
                appt.setEnd(localEndDT.toString());

                apptList.addAll(appt);
            }

        }catch (Exception e){e.printStackTrace();}
        view.setItems(apptList);
    }

    /**
     * No filter radio is selected. Displays all appointments
     * @param e
     * @throws SQLException
     */
    public void noFilterButton(ActionEvent e) throws SQLException {
        populateAppointmentTable();
    }

    /**
     * Filters appointments by current month
     * Lambda expression used to efficiently create an event handler selecting filter, avoiding the need for
     * a new method to populate a list.
     * @param e
     */
    public void monthlyRadioButton(ActionEvent e)
    {
        LocalDate now = LocalDate.now();
        LocalDate nowPlusMonth = now.plusMonths(1);

        //lambda expression used with filtered list
        FilteredList<Appointment> monthFilterList = new FilteredList<>(apptList);
        monthFilterList.setPredicate(select -> {


            //String parsedDate = select.getStart().replace("T"," ");
            //System.out.println(parsedDate);
            LocalDate selectDate = LocalDate.parse(select.getStart(), dtDTF);
            return selectDate.isAfter(now.minusDays(1)) && selectDate.isBefore(nowPlusMonth);
        });

        view.setItems(monthFilterList);
    }

    /**
     * Filters appointments in current week
     * Lambda expression used to efficiently create an event handler selecting filter, avoiding the need for
     * a new method to populate a list.
     * @param e
     */
    public void weeklyRadioButton(ActionEvent e)
    {
        //filter appointments for week
        LocalDate now = LocalDate.now();
        LocalDate nowPlusWeek = now.plusWeeks(1);

        //lambda expression used with filtered list
        FilteredList<Appointment> weeklyFilteredList = new FilteredList<>(apptList);
        weeklyFilteredList.setPredicate(select -> {

            LocalDate selectDate = LocalDate.parse(select.getStart(), dtDTF);

            return selectDate.isAfter(now.minusDays(1)) && selectDate.isBefore(nowPlusWeek);
        });
        view.setItems(weeklyFilteredList);

    }

    public ObservableList<Appointment> getApptList()
    {
        return apptList;
    }

    /**
     * Displays the add appointment menu
     * @param event
     * @throws IOException
     */
    public void onAddApptPressed(ActionEvent event) throws IOException {
        Stage window = (Stage) ((Node) event.getSource()).getScene().getWindow();
        window.close();
        Parent parent = (Parent) FXMLLoader.load(this.getClass().getResource("AppointmentAddView.fxml"));;
        Scene scene = new Scene(parent);
        Stage stage = new Stage();
        stage.setScene(scene);
        stage.show();
    }

    /**
     * Delete button is selected. Warn user
     * @param e
     * @throws SQLException
     */
    public void onDeleteButton(ActionEvent e) throws SQLException {
        Appointment appt = (Appointment) view.getSelectionModel().getSelectedItem();
        if(appt != null)
        {
            Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
            alert.setTitle("Warning Dialog");
            alert.setHeaderText("Are you sure you want to delete Appointment:\n"+appt.getAppointmentID() + ": " + appt.getType() + "?");
            Optional<ButtonType> result = alert.showAndWait();
            if(result.get() == ButtonType.OK) {
                int id = appt.getAppointmentID();
                appt.deleteAppointmentDB(id, appt.getAppointmentID(), appt.getType());
                populateAppointmentTable();
            }

        }
    }

    /**
     * Modify button is pressed. Open modify window
     * @param e
     * @throws IOException
     */
    public void onModifiedButton(ActionEvent e) throws IOException {
        modifiedAppointment = (Appointment) view.getSelectionModel().getSelectedItem();
        if(modifiedAppointment == null)
        {
            Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
            alert.setTitle("Warning Dialog");
            alert.setHeaderText("Please select an Appointment to modify");
            alert.showAndWait();
        }
        else
        {
            Stage window = (Stage) ((Node) e.getSource()).getScene().getWindow();
            window.close();
            Parent parent = (Parent) FXMLLoader.load(this.getClass().getResource("AppointmentModifyView.fxml"));;
            Scene scene = new Scene(parent);
            Stage stage = new Stage();
            stage.setScene(scene);
            stage.show();
        }
    }

    public Appointment getModifiedAppointment()
    {return modifiedAppointment;}

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
    apptList.clear();
    this.idColumn.setCellValueFactory(new PropertyValueFactory<Appointment, Integer>("appointmentID"));
    this.titleColumn.setCellValueFactory(new PropertyValueFactory<Appointment,String>("title"));
    this.descriptionColumn.setCellValueFactory(new PropertyValueFactory<Appointment,String>("description"));
    this.locationColumn.setCellValueFactory(new PropertyValueFactory<Appointment,String>("location"));
    this.typeColumn.setCellValueFactory(new PropertyValueFactory<Appointment,String>("type"));
    this.startColumn.setCellValueFactory(new PropertyValueFactory<Appointment,String>("start"));
    this.endColumn.setCellValueFactory(new PropertyValueFactory<Appointment,String>("end"));
    this.customerIDColumn.setCellValueFactory(new PropertyValueFactory<Appointment,Integer>("customerID"));
    this.userIDColumn.setCellValueFactory(new PropertyValueFactory<Appointment,Integer>("userID"));
    this.contactIDColumn.setCellValueFactory(new PropertyValueFactory<Appointment,Integer>("contactID"));
        try {
            populateAppointmentTable();
        } catch (SQLException throwables) {
            System.out.println("There was an error populating the Appointment table");
        }
        //noFilterButton.setSelected(true);
    }

}
