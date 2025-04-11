package View;

import Model.Countries;
import Model.Customer;
import Model.FirstLevelDivision;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import org.w3c.dom.Text;
import util.DBConnection;

import java.awt.*;
import java.io.IOException;
import java.net.URL;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Optional;
import java.util.ResourceBundle;

public class CustomerAddController implements Initializable {


    @FXML public TextField customerNameField;
    @FXML public TextField customerAddressField;
    @FXML public TextField customerPostalField;
    @FXML public TextField customerPhoneField;
    @FXML public ComboBox<String> countryCombo;
    @FXML public ComboBox<String> divisionCombo;
    @FXML public Button saveButton;
    @FXML public Button cancelButton;

    ObservableList<String> countryList= FXCollections.observableArrayList();
    ObservableList<String> divisionList = FXCollections.observableArrayList();
    private String message = "";

    /**
     * Set the country combo values from the database
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
     * Set first level division combo values from database values
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
     * Check the form for errors and save the customer to the database
     * @param e
     */
    public void onSaveButtonPressed(ActionEvent e)
    {

        //try{
            //First Check if the fields are blank
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
                    //DEBUG

                    //System.out.println(divisionId);


                    String user = UserLoginController.user.getUserName();

                    SimpleDateFormat date = new SimpleDateFormat("yyyy.MM.dd.HH:mm:ss");
                    String timeStamp = date.format(new Date());


                    String insertStatement = "INSERT INTO customers(Customer_Name, Address, Postal_Code, " +
                            "Phone,Create_Date,Created_By,Last_Update,Last_Updated_By,Division_ID) VALUES (?,?,?,?,?,?,?,?,?)";
                    DBConnection.SetPreparedStatement(DBConnection.GetConnection(),insertStatement);
                    PreparedStatement ps = DBConnection.GetPreparedStatement();

                    ps.setString(1, customer.getCustomerName());
                    ps.setString(2,customer.getAddress());
                    ps.setString(3,customer.getPostalCode());
                    ps.setString(4,customer.getPhone());
                    ps.setString(5, timeStamp);
                    ps.setString(6,user);
                    ps.setString(7,timeStamp);
                    ps.setString(8,user);
                    ps.setInt(9,divisionId);
                    ps.executeUpdate();
                    if(ps.getUpdateCount() > 0)
                    {

                        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
                        alert.setTitle("Result");
                        alert.setHeaderText("Customer Added");
                        alert.showAndWait();
                        Parent parent = (Parent) FXMLLoader.load(this.getClass().getResource("CustomerRecordView.fxml"));;
                        Scene scene = new Scene(parent);
                        Stage stage = new Stage();
                        stage.setScene(scene);
                        stage.show();
                        Stage window = (Stage) ((Node) e.getSource()).getScene().getWindow();
                        window.close();
                    }

                    //Set the country and division id's
                    //String updateStatement = ""

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
       //}catch (Exception ex) {ex.printStackTrace();}
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
        if (result.get() == ButtonType.OK) {
            Parent parent = (Parent) FXMLLoader.load(this.getClass().getResource("CustomerRecordView.fxml"));
            Scene scene = new Scene(parent);
            Stage stage = new Stage();
            stage.setScene(scene);
            Stage window = (Stage) ((Node) e.getSource()).getScene().getWindow();
            window.close();
        }
    }

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

    //search for the country id
    public int setCountryID(String country) throws SQLException {
        int id = -1;
        String sqlStatement = "SELECT * FROM countries";
        Statement statement = DBConnection.startConnection().createStatement();
        ResultSet rs = statement.executeQuery(sqlStatement);
        while(rs.next())
        {
            if(rs.getString("Country") == country)
                id = rs.getInt("Country_ID");
        }

        return id;
    }

    //Search for the division id
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
        try {
            setCountryComboValues();
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
    }

}
