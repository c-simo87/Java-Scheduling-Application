package View;

import Model.Appointment;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import util.DBConnection;

import java.net.URL;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ResourceBundle;

public class AppointmentReportController implements Initializable {
@FXML TableView typeView;
@FXML TableColumn<Appointment,String> typeColumn;
@FXML TableColumn<Appointment,Integer> typeTotalColumn;

@FXML TableView monthView;
@FXML TableColumn<Appointment,String> monthColumn;
@FXML TableColumn<Appointment,Integer> monthTotalColumn;

    private ObservableList<Appointment> apptTypeList = FXCollections.observableArrayList();
    private ObservableList<Appointment> apptMonthList = FXCollections.observableArrayList();

    /**
     * Display the report type window after query is ran
     * @throws SQLException
     */
    public void showTypeReport() throws SQLException {
        try {
            //String sqlStatement = "SELECT Type,Count(Type) as Total from appointments group by Type with rollup";
            String sqlStatement = "SELECT Type, COUNT(Type) as \"Total\" FROM appointments GROUP BY Type";
            Statement statement = DBConnection.startConnection().createStatement();
            ResultSet rs = statement.executeQuery(sqlStatement);
            while(rs.next())
            {
                Appointment appt = new Appointment();
                appt.setType(rs.getString("Type"));
                appt.setTotal(rs.getInt("Total"));
                apptTypeList.add(appt);
            }
            typeView.setItems(apptTypeList);


        }catch (Exception e){e.printStackTrace();showError("Error in SQL syntax"); }
    }

    /**
     * Display month window report from data base query
     */
    public void showMonthReport()
    {
        try {
            //String sqlStatement = "SELECT Type,Count(Type) as Total from appointments group by Type with rollup";
            String sqlStatement = "SELECT monthname(Start) as \"Month\", COUNT(MONTH(Start)) as \"Total\" from appointments GROUP BY Month";
            Statement statement = DBConnection.startConnection().createStatement();
            ResultSet rs = statement.executeQuery(sqlStatement);
            String total = "";
            while(rs.next())
            {
                Appointment appt = new Appointment();
                appt.setMonth(rs.getString("Month"));
                appt.setTotal(rs.getInt("Total"));
                apptMonthList.add(appt);
            }
            monthView.setItems(apptMonthList);


        }catch (Exception e){e.printStackTrace();showError("Error in SQL syntax"); }
    }

public void showError(String error)
{
    Alert alert = new Alert(Alert.AlertType.WARNING);
    alert.setTitle("");
    alert.setHeaderText(error);
    alert.show();
}
    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        apptTypeList.clear();
        apptMonthList.clear();
        this.typeColumn.setCellValueFactory(new PropertyValueFactory<Appointment, String>("type"));
        this.typeTotalColumn.setCellValueFactory(new PropertyValueFactory<Appointment,Integer>("total"));
        this.monthColumn.setCellValueFactory(new PropertyValueFactory<Appointment, String>("month"));
        this.monthTotalColumn.setCellValueFactory(new PropertyValueFactory<Appointment,Integer>("total"));
        try {
            showTypeReport();
            showMonthReport();
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
    }
}
