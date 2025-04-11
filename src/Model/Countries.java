package Model;

import util.DBConnection;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class Countries {
    private int countryID;
    private String country;
    private String createDate;
    private String createdBy;
    private String lastUpdatedBy;

    public Countries(int countryID, String country, String create_date, String createdBy, String lastUpdatedBy) {
        this.countryID = countryID;
        this.country = country;
        this.createDate = create_date;
        this.createdBy = createdBy;
        this.lastUpdatedBy = lastUpdatedBy;
    }

    public Countries(){}

    public void setCountry_UD(int country_UD) {
        this.countryID = country_UD;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    public void setCreateDate(String create_date) {
        this.createDate = create_date;
    }

    public void setCreatedBy(String createdBy) {
        this.createdBy = createdBy;
    }

    public void setLastUpdatedBy(String lastUpdatedBy) {
        this.lastUpdatedBy = lastUpdatedBy;
    }

    public int getCountry_UD() {
        return countryID;
    }

    public String getCountry() {
        return country;
    }

    public String getCreate_date() {
        return createDate;
    }

    public String getCreatedBy() {
        return createdBy;
    }

    public String getLastUpdatedBy() {
        return lastUpdatedBy;
    }

    public int searchCountryID(String country) throws SQLException {
        int id = -1;
        String sqlStatement = "SELECT * FROM countries";
        Statement statement = DBConnection.startConnection().createStatement();
        ResultSet rs = statement.executeQuery(sqlStatement);
        while (rs.next()) {
            if (rs.getString("Country") == country)
                id = rs.getInt("Country_ID");
        }

        return id;
    }
}
