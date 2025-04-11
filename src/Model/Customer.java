package Model;

import javafx.collections.ObservableList;
import javafx.scene.control.Alert;
import util.DBConnection;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class Customer {
    private int customerId;
    private String customerName;
    private String address;
    private String postalCode;
    private String phone;
    private String createDate;
    private String createdBy;
    private String lastUpdate;
    private String lastUpdatedBy;
    private String country;
    private String division;
    private int divisionId;
    //Report Vars
    private String month;
    private int total;


    public Customer(int customerId, String customerName, String address, String postalCode, String phone, String createDate,
                    String createdBy, String lastUpdate, String lastUpdatedBy, String country,String division, int divisionId) {
        this.customerId = customerId;
        this.customerName = customerName;
        this.address = address;
        this.postalCode = postalCode;
        this.phone = phone;
        this.createDate = createDate;
        this.createdBy = createdBy;
        this.lastUpdate = lastUpdate;
        this.lastUpdatedBy = lastUpdatedBy;
        this.country = country;
        this.division = division;
        this.divisionId = divisionId;
    }

    public Customer(){}

    public int getTotal(){return total;}

    public void setTotal(int i){this.total = i;}

    public String getMonth(){return this.month;}

    public void setMonth(String m){this.month = m;}

    public int getCustomerId() {
        return customerId;
    }

    public void setCustomerId(int customerId) {
        this.customerId = customerId;
    }

    public String getCustomerName() {
        return customerName;
    }

    public void setCustomerName(String customerName) {
        this.customerName = customerName;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getPostalCode() {
        return postalCode;
    }

    public void setPostalCode(String postalCode) {
        this.postalCode = postalCode;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
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

    public String getLastUpdatedBy() {
        return lastUpdatedBy;
    }

    public void setLastUpdatedBy(String lastUpdatedBy) {
        this.lastUpdatedBy = lastUpdatedBy;
    }

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    public String getDivision() {
        return division;
    }

    public void setDivision(String division) {
        this.division = division;
    }

    public int getDivisionId() {
        return divisionId;
    }

    public void setDivisionIdDB(int divisionId) {
        this.divisionId = divisionId;
    }

    public static Integer searchCustomerID(String customer) throws SQLException {
        int id = -1;
        try {
            String sqlStatement = "SELECT * from customers where customers.customer_name = \"" + customer + "\"";
            Statement statement = DBConnection.startConnection().createStatement();
            ResultSet rs = statement.executeQuery(sqlStatement);
            while(rs.next())
            {
                id = rs.getInt("Customer_ID");
            }
        }catch (Exception e) {e.printStackTrace();}
        return id;
    }
    public void deleteCustomerDB(Customer c) throws SQLException {
        String sqlStatement = "DELETE from customers Where customer_id = \"" + c.getCustomerId() + "\"";
        DBConnection.SetPreparedStatement(DBConnection.GetConnection(), sqlStatement);
        PreparedStatement ps = DBConnection.GetPreparedStatement();
        ps.executeUpdate();
        if(ps.getUpdateCount() > 0)
        {
            Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
            alert.setTitle("Result");
            alert.setHeaderText("Customer Deleted");
            alert.showAndWait();
        }
    }

    public static void getAllCustomers(ObservableList c)
    {
        try {
            String sqlStatement = "SELECT distinct Customer_Name from customers";
            Statement statement = DBConnection.startConnection().createStatement();
            ResultSet rs = statement.executeQuery(sqlStatement);
            while (rs.next()) {
                Customer customer = new Customer();
                customer.setCustomerName(rs.getString("Customer_Name"));
                c.add(customer.getCustomerName());
            }
        }catch (Exception e) {e.printStackTrace();}

    }
}
