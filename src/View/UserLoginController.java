package View;

import Model.Appointment;
import Model.Logger;
import Model.User;
import com.mysql.cj.protocol.Resultset;
import com.mysql.cj.x.protobuf.MysqlxPrepare;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import util.DBConnection;
import java.awt.*;
import java.io.Console;
import java.io.IOException;
import java.net.URL;
import java.sql.*;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Optional;
import java.util.ResourceBundle;
import java.util.Date;
import javafx.scene.Node;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Label;
import javafx.scene.control.RadioButton;
import javafx.scene.control.TextField;
import javafx.scene.control.ToggleGroup;
import javafx.scene.control.Alert.AlertType;

public class UserLoginController implements Initializable {

    @FXML public Button loginButton;

    @FXML public Button exitButton;

    @FXML public TextField userField;

    @FXML public TextField passwordField;

    @FXML public Label userLabel;

    @FXML public Label passLabel;

    @FXML public Label title;

    @FXML public Label zoneIdLabel;

    private static ZoneId zoneId = ZoneId.systemDefault();
    private ObservableList<Appointment> appointmentTimes = FXCollections.observableArrayList();
    public static User user = new User();
    public static Logger logger = new Logger();
    ResourceBundle rb;
    Connection c = DBConnection.startConnection();

    /**
     * Program will check for a balid user id and password
     * Lambda expression used to efficiently utilize a foreach loop, reducing code
     * @param e
     * @throws SQLException
     * @throws IOException
     */
    public void onLoginButtonPressed(ActionEvent e) throws SQLException, IOException {
        String username = userField.getText();
        String password = passwordField.getText();
        int userId = getUser(username,password);
        if(userId > -1)
        {
            System.out.println("Login successful");
            logger.appendFile(username,true);
            user.setUserId(userId);
            user.setUserName(username);
            user.setZoneId(zoneId);
            getAppointmentWarning();
            if(appointmentTimes.size() > 0)
            {
                //Lambda Expression
                appointmentTimes.forEach(upcoming -> {
                   String message = "Title:  "+ upcoming.getTitle() + " ID: " + upcoming.getAppointmentID() + " Start: " +  upcoming.getStart();
                    errorMessage("Appointment Reminder","Alert: You have an upcoming Appointment", message);
                });

            }
            else{
                errorMessage("Appointment Reminder" , "Alert", "There are no upcoming appointments");
            }
            Parent parent = (Parent) FXMLLoader.load(this.getClass().getResource("MainMenuView.fxml"));;
            Scene scene = new Scene(parent);
            Stage stage = new Stage();
            stage.setScene(scene);
            stage.show();
        }
        else
        {
            logger.appendFile(username,false);
            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.setTitle("");
            alert.setHeaderText(rb.getString("error"));
            alert.show();
        }
    }

    /**
     * Generate error message method
     * @param message
     * @param title
     * @param header
     */
    public void errorMessage(String message, String title, String header)
    {
        Alert alert = new Alert(Alert.AlertType.WARNING);
        alert.setTitle(title);
        alert.setHeaderText(header);
        alert.setContentText(message);
        alert.showAndWait();
    }

    /**
     * User exits program
     * @param e
     */
    public void onExitPressed(ActionEvent e)
    {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Warning Dialog");
        alert.setHeaderText("Are you sure you would like to exit?");
        Optional<ButtonType> result = alert.showAndWait();
        if (result.get() == ButtonType.OK) {
            // ... user chose OK
            Stage window = (Stage) ((Node) e.getSource()).getScene().getWindow();
            window.close();
        } else {
            // ... user pressed cancel
        }
    }

        /**
         * Search the data base for the user id and password
         * @param un
         * @param pw
         * @return
         * @throws SQLException
         */
    public int getUser(String un, String pw) throws SQLException {
        int userId = -1;
        try {
            String inputStatement = "SELECT User_ID,User_Name,Password FROM users";
            Statement s = c.createStatement();
            ResultSet rs = s.executeQuery(inputStatement);
            while (rs.next())
                if (rs.getString("User_Name").equals(un) && rs.getString("Password").equals(pw)) {
                    userId = rs.getInt("User_ID");
                }
        }catch (Exception e){e.printStackTrace();}
        return userId;
    }

    /**
     * Search the database for an appointment 15 minutes from sign in
     */
    public void getAppointmentWarning()
    {
        try{
            String sqlStatement = "SELECT Appointment_ID,Title,Start,End,User_ID FROM appointments WHERE Start BETWEEN ? AND ? AND User_ID = ? ";
            PreparedStatement ps = c.prepareStatement(sqlStatement);
            DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
            LocalDateTime now = LocalDateTime.now();
            ZonedDateTime userTZnow = now.atZone(user.getZoneId());
            ZonedDateTime nowUTC = userTZnow.withZoneSameInstant(ZoneOffset.UTC);
            ZonedDateTime userTZPlus15 = userTZnow.plusMinutes(15);
            ZonedDateTime utcPlus15 = nowUTC.plusMinutes(15);
            String startString = nowUTC.format(dtf);
            String convertStart = userTZnow.format(dtf);
            String endString = utcPlus15.format(dtf);
            int id = user.getUserId();
            //System.out.print(userTZnow + " " + utcPlus15 + "\n String " + startString + " " + endString);

            ps.setString(1,startString);
            ps.setString(2,endString);
            ps.setInt(3,id);
            ps.executeQuery();
            ResultSet rs = ps.getResultSet();
            while(rs.next())
            {
                Appointment appt = new Appointment();
                appt.setTitle(rs.getString("Title"));
                //appt.setStart(rs.getString("Start"));//startString);
                appt.setStart(convertStart);
                appt.setEnd(rs.getString("End"));//endString);
                appt.setUserID(id);
                appt.setAppointmentID(rs.getInt("Appointment_ID"));
                appointmentTimes.add(appt);
            }


        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
    this.rb = resourceBundle;
    try {
        loginButton.setText(rb.getString("login"));
        exitButton.setText(rb.getString("exit"));
        userLabel.setText(rb.getString("username"));
        passLabel.setText(rb.getString("password"));
        title.setText(rb.getString("title"));
        zoneIdLabel.setText("Currently Location: " + zoneId.toString());
    }catch (Exception e) {System.out.println("Could not set the label");}
    }

    public ZoneId getZoneId() {
        return zoneId;
    }
}
