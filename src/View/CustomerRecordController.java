package View;

import Model.Countries;
import Model.Customer;
import Model.FirstLevelDivision;
import com.mysql.cj.protocol.Resultset;
import com.mysql.cj.xdevapi.Table;
import javafx.beans.Observable;
import javafx.beans.property.Property;
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
import javafx.scene.control.Button;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;
import util.DBConnection;

import javax.swing.*;
import java.awt.*;
import java.io.IOException;
import java.sql.*;

import java.net.URL;
import java.util.Optional;
import java.util.ResourceBundle;

public class CustomerRecordController implements Initializable {

@FXML public TableView<Customer> customerTableView;
@FXML public TableColumn<Customer, Integer> customerIdColumn;
@FXML public TableColumn<Customer, String> customerNameColumn;
@FXML public TableColumn<Customer, String> customerAddressColumn;
@FXML public TableColumn<Customer, String> customerPostalColumn;
@FXML public TableColumn<Customer, String> customerPhoneColumn;
@FXML public TableColumn<Customer, String> customerCountryColumn;
@FXML public TableColumn<Customer, String> divisionColumn;
@FXML public Button addCustomerButton;
@FXML public Button modifyCustomerButton;
@FXML public Button deleteCustomerButton;

ObservableList<Customer> customerList= FXCollections.observableArrayList();
private static Customer modifiedCustomer;

public CustomerRecordController(){}

    /**
     * Initiate database query to populate table view with customers
     * @throws SQLException
     */
    public void populateCustomerTable() throws SQLException {
    customerList.clear();
    try {
        String sqlStatement = "SELECT Customer_ID, Customer_Name, Address, Phone,Postal_Code, Division, Country FROM customers,countries,first_level_divisions " +
                "WHERE first_level_divisions.COUNTRY_ID = countries.Country_ID AND customers.Division_ID = first_level_divisions.Division_ID ORDER BY Customer_ID ASC";
        Statement s = DBConnection.startConnection().createStatement();
        ResultSet rs = s.executeQuery(sqlStatement);
        while(rs.next())
        {
            FirstLevelDivision fld = new FirstLevelDivision();
            Customer customer = new Customer();
            customer.setCustomerId(rs.getInt("Customer_ID"));
            customer.setCustomerName(rs.getString("Customer_Name"));
            customer.setAddress(rs.getString("Address"));
            customer.setPhone(rs.getString("Phone"));
            customer.setPostalCode(rs.getString("Postal_Code"));
            customer.setCountry(rs.getString("Country"));
            customer.setDivision(rs.getString("Division"));
            customerList.addAll(customer);
        }
    }catch (Exception e){e.printStackTrace();}
    customerTableView.setItems(customerList);
}

    /**
     * Customer add screen is selected
     * @param actionEvent
     * @throws IOException
     */
    public void onCustomerAdd(javafx.event.ActionEvent actionEvent) throws IOException {
        Stage window = (Stage) ((Node) actionEvent.getSource()).getScene().getWindow();
        window.close();
        Parent parent = (Parent) FXMLLoader.load(this.getClass().getResource("CustomerAddView.fxml"));;
        Scene scene = new Scene(parent);
        Stage stage = new Stage();
        stage.setScene(scene);
        stage.show();
    }

    /**
     * Customer delete button is selected
     * @param e
     * @throws IOException
     * @throws SQLException
     */
    public void onCustomerDelete(ActionEvent e) throws IOException, SQLException {
    Customer delete = customerTableView.getSelectionModel().getSelectedItem();
    if(delete == null)
    {
        Alert alert = new Alert(Alert.AlertType.WARNING);
        alert.setTitle("Warning Dialog");
        alert.setHeaderText("Invalid Selection");
        alert.setContentText("Please select a customer to delete");
        alert.show();
    }
    else
    {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Warning Dialog");
        alert.setHeaderText("Are you sure you want to delete Customer: " + delete.getCustomerName() + "?");
        Optional<ButtonType> result = alert.showAndWait();
        if(result.get() == ButtonType.OK) {
            try {
                delete.deleteCustomerDB(delete);
                populateCustomerTable();
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }

    }
}

    /**
     * Customer modify button is selected
     * @param actionEvent
     * @throws IOException
     */
    public void onCustomerModify(javafx.event.ActionEvent actionEvent) throws IOException {
        modifiedCustomer = customerTableView.getSelectionModel().getSelectedItem();
        if(modifiedCustomer == null)
        {
            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.setTitle("Warning Dialog");
            alert.setHeaderText("Invalid Selection");
            alert.setContentText("Please select a customer to modify");
            alert.show();
        }
        else
        {
            Stage window = (Stage) ((Node) actionEvent.getSource()).getScene().getWindow();
            window.close();
            Parent parent = (Parent) FXMLLoader.load(this.getClass().getResource("CustomerModifyView.fxml"));;
            Scene scene = new Scene(parent);
            Stage stage = new Stage();
            stage.setScene(scene);
            stage.show();
        }
    }

public void initialize(URL url, ResourceBundle rb)
{
    this.customerIdColumn.setCellValueFactory(new PropertyValueFactory<Customer, Integer>("customerId"));
    this.customerNameColumn.setCellValueFactory(new PropertyValueFactory<Customer,String>("customerName"));
    this.customerAddressColumn.setCellValueFactory(new PropertyValueFactory<Customer,String>("address"));
    this.customerPhoneColumn.setCellValueFactory(new PropertyValueFactory<Customer,String>("phone"));
    this.customerPostalColumn.setCellValueFactory(new PropertyValueFactory<Customer, String>("postalCode"));
    this.customerCountryColumn.setCellValueFactory(new PropertyValueFactory<Customer,String>("country"));
    this.divisionColumn.setCellValueFactory(new PropertyValueFactory<Customer,String>("division"));
    //this.divisionIdColumn.setCellValueFactory(new PropertyValueFactory<FirstLevelDivision,String>("division"));
    try {
        populateCustomerTable();
    } catch (SQLException throwables) {
        throwables.printStackTrace();
    }
}



    public Customer getModifiedCustomer()
    {
        return modifiedCustomer;
    }


}
