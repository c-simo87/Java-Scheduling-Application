package Model;

import View.UserLoginController;
import javafx.collections.ObservableList;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.ComboBox;
import javafx.scene.control.ComboBoxBase;
import javafx.stage.Stage;
import util.DBConnection;

import java.sql.*;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

public class Appointment {
    private int appointmentID;
    private String title;
    private String description;
    private String location;
    private String type;
    private String start;
    private String end;
    private String createDate;
    private String createdBy;
    private String lastUpdate;
    private String lastUpdateBy;
    private int userID;
    private int customerID;
    private int contactID;
    // For Reports
    private int total;
    private String month;


    public Appointment(int appointmentID, String title, String description, String location,
                       String type, String start, String end, String createDate, String createdBy, String lastUpdate,
                       String lastUpdateBy,int userID, int customerID, int contactID) {
        this.appointmentID = appointmentID;
        this.title = title;
        this.description = description;
        this.location = location;
        this.type = type;
        this.start = start;
        this.end = end;
        this.createDate = createDate;
        this.createdBy = createdBy;
        this.lastUpdate = lastUpdate;
        this.lastUpdateBy = lastUpdateBy;
        this.userID = userID;
        this.customerID = customerID;
        this.contactID = contactID;
    }
    public Appointment(){}

    public int getTotal(){return total;}

    public void setTotal(int t){this.total = t;}

    public void setMonth(String m){this.month = m;}

    public String getMonth(){return month;}

    public int getAppointmentID() {
        return appointmentID;
    }

    public int getUserID() {
        return userID;
    }

    public void setUserID(int userID) {
        this.userID = userID;
    }

    public void setAppointmentID(int appointmentID) {
        this.appointmentID = appointmentID;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getStart() {
        return start;
    }

    public void setStart(String start) {
        this.start = start;
    }

    public String getEnd() {
        return end;
    }

    public void setEnd(String end) {
        this.end = end;
    }

    public String getCreateDate() {
        return createDate;
    }

    public void setCreateDate(String createDate) {
        this.createDate = createDate;
    }

    public String getCreatedBy() {
        return createdBy;
    }

    public void setCreatedBy(String createdBy) {
        this.createdBy = createdBy;
    }

    public String getLastUpdate() {
        return lastUpdate;
    }

    public void setLastUpdate(String lastUpdate) {
        this.lastUpdate = lastUpdate;
    }

    public String getLastUpdateBy() {
        return lastUpdateBy;
    }

    public void setLastUpdateBy(String lastUpdateBy) {
        this.lastUpdateBy = lastUpdateBy;
    }

    public int getCustomerID() {
        return customerID;
    }

    public void setCustomerID(int customerID) {
        this.customerID = customerID;
    }

    public int getContactID() {
        return contactID;
    }

    public void setContactID(int contactID) {
        this.contactID = contactID;
    }

    public void deleteAppointmentDB(int id, int apptId, String type) throws SQLException {
        String sqlStatement = "DELETE FROM appointments WHERE Appointment_ID = ?";
        DBConnection.SetPreparedStatement(DBConnection.GetConnection(),sqlStatement);
        PreparedStatement ps = DBConnection.GetPreparedStatement();
        ps.setInt(1,id);
        ps.executeUpdate();
        if(ps.getUpdateCount() > 0)
        {
            Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
            alert.setTitle("Result");
            alert.setHeaderText("Appointment: " + apptId + ": " + type + " deleted" );
            alert.showAndWait();
        }

    }

    public void modifyAppointment(int id, String title, String description, String location, String type, Timestamp startDateTime, Timestamp endDateTime, Timestamp createDate,
                                  Timestamp lastUpdated, String lastUpdatedBy, Integer customerID,  Integer contactID) throws SQLException {
        String sqlStatement = "UPDATE appointments SET Title = ?, Description = ?, Location = ?, Type = ?, Start = ?, End = ?, Create_Date = ?, Last_Update = ?, Last_Updated_By = ?," +
                " Customer_ID = ?,  Contact_ID = ? WHERE Appointment_ID = \"" + id +  "\"";
        DBConnection.SetPreparedStatement(DBConnection.GetConnection(),sqlStatement);
        PreparedStatement ps = DBConnection.GetPreparedStatement();
        ps.setString(1,title);
        ps.setString(2,description);
        ps.setString(3,location);
        ps.setString(4,type);
        ps.setTimestamp(5,startDateTime);
        ps.setTimestamp(6,endDateTime);
        ps.setTimestamp(7,createDate);
        ps.setTimestamp(8,lastUpdated);
        ps.setString(9,lastUpdatedBy);
        ps.setInt(10, customerID);
        //ps.setInt(11,userID);
        ps.setInt(11,contactID);


        ps.executeUpdate();
        if(ps.getUpdateCount() > 0)
        {
            Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
            alert.setTitle("Result");
            alert.setHeaderText("Appointment Updated");
            alert.showAndWait();
        }
    }

    public void addAppointment(String title, String description, String location, String type, Timestamp startDateTime, Timestamp endDateTime, Timestamp createDate,
                               Timestamp lastUpdated, String lastUpdatedBy, Integer customerID, Integer userID, Integer contactID) throws SQLException {
        try {
            String alterStmt = "ALTER TABLE appointments AUTO_INCREMENT=1";
            Statement s = DBConnection.GetConnection().createStatement();
            s.execute(alterStmt);

            String sqlStatement = "INSERT INTO appointments(title, description, location, type, start,end,create_date,last_update, last_updated_by, customer_id,user_id," +
                    "contact_id) VALUES(?,?,?,?,?,?,?,?,?,?,?,?)";
            DBConnection.SetPreparedStatement(DBConnection.GetConnection(),sqlStatement);
            PreparedStatement ps = DBConnection.GetPreparedStatement();
            ps.setString(1,title);
            ps.setString(2,description);
            ps.setString(3,location);
            ps.setString(4,type);
            ps.setTimestamp(5,startDateTime);
            ps.setTimestamp(6,endDateTime);
            ps.setTimestamp(7,createDate);
            ps.setTimestamp(8,lastUpdated);
            ps.setString(9,lastUpdatedBy);
            ps.setInt(10, customerID);
            ps.setInt(11,userID);
            ps.setInt(12,contactID);

            ps.executeUpdate();
            if(ps.getUpdateCount() > 0)
            {
                Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
                alert.setTitle("Result");
                alert.setHeaderText("Appointment Added");
                alert.showAndWait();
            }

        }catch(Exception e){e.printStackTrace();}

    }





}
