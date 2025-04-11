package View;

import Model.Countries;
import Model.Customer;
import Model.FirstLevelDivision;
import View.CustomerRecordController;
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

import java.net.URL;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Optional;
import java.util.ResourceBundle;

public class CustomerModifyController implements Initializable {
    @FXML public TextField customerNameField;
    @FXML public TextField customerAddressField;
    @FXML public TextField customerPostalField;
    @FXML public TextField customerPhoneField;
    @FXML public TextField customerIDField;
    @FXML public ComboBox<String> countryCombo;
    @FXML public ComboBox<String> divisionCombo;
    @FXML public Button saveButton;
    @FXML public Button cancelButton;

    ObservableList<String> countryList= FXCollections.observableArrayList();
    ObservableList<String> divisionList = FXCollections.observableArrayList();
    private Customer modifiedCustomer;
    private String message = "";
    CustomerRecordController recordController = new CustomerRecordController();

    /**
     * Populate the country values in the combo list
     * @throws SQLException
     */
    public void setCountryComboValues() throws SQLException {
        try {
            String sqlStatement = "Select Country from countries";
            Statement s = DBConnection.startConnection().createStatement();
            ResultSet rs = s.executeQuery(sqlStatement);
            while(rs.next())
            {
                Countries country = new Countries();
                country.setCountry(rs.getString("Country"));
                countryList.addAll(country.getCountry());
            }
        }catch (Exception e){e.printStackTrace();}
        //comboCountries.setItems(countryList);

        countryCombo.setItems(countryList);

    }

    /**
     * Populate first level division data in combo list
     * @throws SQLException
     */
    public void setFirstLevelDivisionCombo() throws SQLException{
        try {
            if(countryCombo.getSelectionModel().getSelectedItem() != null) {
                divisionList.clear();
                String country = countryCombo.getSelectionModel().getSelectedItem();
                String sqlStatement = "SELECT Division,Country FROM first_level_divisions,countries" +
                        " WHERE first_level_divisions.COUNTRY_ID = countries.Country_ID AND " +
                        "countries.country = \"" + country + "\"";
                Statement s = DBConnection.startConnection().createStatement();
                ResultSet rs = s.executeQuery(sqlStatement);
                while (rs.next()) {
                    FirstLevelDivision fd = new FirstLevelDivision();
                    fd.setDivision(rs.getString("Division"));
                    divisionList.addAll(fd.getDivision());
                }
            }
        }catch (Exception e){e.printStackTrace();}
        //comboCountries.setItems(countryList);

        divisionCombo.setItems(divisionList);
    }


    /**
     * When save button is pressed, run a prepared statement and verify all filled data
     * @param actionEvent
     */
    public void onSaveButtonPressed(ActionEvent actionEvent) {
        if(noErrors())
        {
            try {
                Customer customer = new Customer();
                customer.setCustomerName(customerNameField.getText());
                customer.setAddress(customerAddressField.getText());
                customer.setPostalCode(customerPostalField.getText());
                customer.setPhone(customerPhoneField.getText());
                customer.setCountry(countryCombo.getSelectionModel().getSelectedItem());
                customer.setDivision(divisionCombo.getSelectionModel().getSelectedItem());

                System.out.println("Customer class info " + customer.getCountry() + " " + customer.getDivision());

                //int countryID = setCountryID(customer.getCountry());
                int divisionId = setDivisionID(customer.getDivision());


                String user = UserLoginController.user.getUserName();

                SimpleDateFormat date = new SimpleDateFormat("yyyy.MM.dd.HH:mm:ss");
                String timeStamp = date.format(new Date());


                String insertStatement = "UPDATE customers SET Customer_Name = ?, Address = ?, Postal_Code = ?, Phone = ?, Last_Update = ?, Last_Updated_By = ?, " +
                        "Division_ID = ? WHERE Customer_ID = \"" + modifiedCustomer.getCustomerId()+  "\"";

                DBConnection.SetPreparedStatement(DBConnection.GetConnection(),insertStatement);
                PreparedStatement ps = DBConnection.GetPreparedStatement();

                ps.setString(1, customer.getCustomerName());
                ps.setString(2,customer.getAddress());
                ps.setString(3,customer.getPostalCode());
                ps.setString(4,customer.getPhone());
                ps.setString(5, timeStamp);
                ps.setString(6,user);
                ps.setInt(7,divisionId);
                ps.executeUpdate();
                if(ps.getUpdateCount() > 0)
                {
                    Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
                    alert.setTitle("Result");
                    alert.setHeaderText("Customer Modified");
                    alert.showAndWait();
                    Parent parent = (Parent) FXMLLoader.load(this.getClass().getResource("CustomerRecordView.fxml"));;
                    Scene scene = new Scene(parent);
                    Stage stage = new Stage();
                    stage.setScene(scene);
                    stage.show();
                    Stage window = (Stage) ((Node) actionEvent.getSource()).getScene().getWindow();
                    window.close();
                }

            }catch (Exception sql) {sql.printStackTrace();}
        }
        else
        {
            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.setTitle("Warning Dialog");
            alert.setHeaderText("Invalid Entries");
            alert.setContentText(message);
            alert.show();
        }
    }

    /**
     * User presses cancel and warns on data loss
     * @param actionEvent
     */
    public void onCancelPressed(ActionEvent actionEvent) {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Warning Dialog");
        alert.setHeaderText("Data is unsaved, are you sure you would like to cancel?");
        Optional<ButtonType> result = alert.showAndWait();
        if (result.get() == ButtonType.OK) {
            // ... user chose OK
            Stage window = (Stage) ((Node) actionEvent.getSource()).getScene().getWindow();
            window.close();
        } else {
            // ... user pressed cancel
        }
    }

    /**
     * Check the form for any errors and generate a message
     * @return
     */
    public boolean noErrors()
    {
        message = "";
        if(countryCombo.getSelectionModel().getSelectedItem() == null)
            message += "Please select a Country\n";
        if(divisionCombo.getSelectionModel().getSelectedItem() == null)
            message += "Please select a Division\n";
        if(customerNameField.getText().isEmpty())
            message += "Please enter a Name\n";
        if(customerAddressField.getText().isEmpty())
            message += "Please enter an Address\n";
        if(customerPhoneField.getText().isEmpty())
            message += "Please enter a Phone Number\n";
        if(customerPostalField.getText().isEmpty())
            message += "Please enter a Postal Code\n";
        if(message.length() > 0)
            return false;
        return true;
    }

    /**
     * Query the database and set the division ID accordingly
     * @param division
     * @return
     * @throws SQLException
     */
    public int setDivisionID(String division) throws SQLException {
        int id = -1;
        String sqlStatement = "SELECT * FROM first_level_divisions where first_level_divisions.Division = \"" + division + "\"" ;
        Statement statement = DBConnection.startConnection().createStatement();
        ResultSet rs = statement.executeQuery(sqlStatement);
        while(rs.next())
        {
            id = rs.getInt("Division_ID");
        }

        return id;
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        modifiedCustomer = recordController.getModifiedCustomer();
        System.out.println(modifiedCustomer.getCustomerId() + " " + modifiedCustomer.getCustomerName());
        customerIDField.setText(Integer.toString(modifiedCustomer.getCustomerId()));
        customerNameField.setText(modifiedCustomer.getCustomerName());
        customerAddressField.setText(modifiedCustomer.getAddress());
        customerPhoneField.setText(modifiedCustomer.getPhone());
        customerPostalField.setText(modifiedCustomer.getPostalCode());
        try {
            setCountryComboValues();
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
        try {
            setFirstLevelDivisionCombo();
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
        countryCombo.setValue(modifiedCustomer.getCountry());
        divisionCombo.setValue(modifiedCustomer.getDivision());
    }
}
