package Model;

import javafx.collections.ObservableList;
import util.DBConnection;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class Contact {
    private int contactId;
    private String contactName;
    private String Email;

    public Contact(int contactId, String contactName, String email) {
        this.contactId = contactId;
        this.contactName = contactName;
        Email = email;
    }
    public Contact(){}

    public int getContactId() {
        return contactId;
    }

    public void setContactId(int contactId) {
        this.contactId = contactId;
    }

    public String getContactName() {
        return contactName;
    }

    public void setContactName(String contactName) {
        this.contactName = contactName;
    }

    public String getEmail() {
        return Email;
    }

    public void setEmail(String email) {
        Email = email;
    }

    public static Integer searchContactID(String contact) throws SQLException {
        int id = -1;
        try {
            String sqlStatement = "SELECT * from contacts where Contact_Name = \"" + contact + "\"";
            Statement statement = DBConnection.startConnection().createStatement();
            ResultSet rs = statement.executeQuery(sqlStatement);
            while(rs.next())
            {
                id = rs.getInt("Contact_ID");
            }
        }catch (Exception e) {e.printStackTrace();}
        return id;
    }

//primarily for combo box reports - returns the list of STRING names
    public static void getAllContacts(ObservableList list)
    {
        try {
            String sqlStatement = "SELECT distinct contact_Name from contacts";
            Statement statement = DBConnection.startConnection().createStatement();
            ResultSet rs = statement.executeQuery(sqlStatement);
            while (rs.next()) {
                Contact contact = new Contact();
                contact.setContactName(rs.getString("contact_name"));
                list.add(contact.getContactName());
            }
        }catch (Exception e) {e.printStackTrace();}

    }
}
