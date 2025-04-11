package View;

import Model.Appointment;
import Model.Customer;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;

import javafx.scene.control.cell.PropertyValueFactory;
import util.DBConnection;

import java.net.URL;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ResourceBundle;

public class CustomerReportController implements Initializable {
    @FXML
    TableView tableView;
    @FXML
    TableColumn<Customer,String> monthColumn;
    @FXML TableColumn<Customer,Integer> totalColumn;
    @FXML
    ComboBox<String> customerCombo;

    ObservableList<String> customerReportList = FXCollections.observableArrayList();
    ObservableList<Customer> customerList = FXCollections.observableArrayList();

    /**
     * Set the view with the database queries for customer reports
     * @param customerName
     * @throws SQLException
     */
    public void setTableView(String customerName) throws SQLException {
        try {
            int customerID = Customer.searchCustomerID(customerName);
            String sqlStatement = "SELECT monthname(Start) as \"Month\", COUNT(MONTH(Start)) as \"Total\" from appointments Where Customer_ID = \""+customerID + "\" GROUP BY Month";
            Statement statement = DBConnection.startConnection().createStatement();
            ResultSet rs = statement.executeQuery(sqlStatement);
            while (rs.next()) {
                Customer customer = new Customer();
                customer.setMonth(rs.getString("Month"));
                customer.setTotal(rs.getInt("Total"));
                customerList.add(customer);
            }
            tableView.setItems(customerList);
        }catch (Exception e){e.printStackTrace();}
    }

    /**
     * When the customer is selected, pass the selection to setTableView()
     * @throws SQLException
     */
    public void onCustomerSelect() throws SQLException {
        customerList.clear();
        String customerName = customerCombo.getSelectionModel().getSelectedItem();
        setTableView(customerName);
    }

    /**
     * Set the customer combo values
     */
    public void setCustomerCombo()
    {
        Customer.getAllCustomers(customerReportList);
        customerCombo.setItems(customerReportList);
    }


    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        this.monthColumn.setCellValueFactory(new PropertyValueFactory<Customer, String>("month"));
        this.totalColumn.setCellValueFactory(new PropertyValueFactory<Customer,Integer>("total"));
        customerReportList.clear();
        setCustomerCombo();
    }
}
