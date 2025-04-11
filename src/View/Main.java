package View;

import View.UserLoginController;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import util.DBConnection;

import java.io.IOException;
import java.sql.Connection;
import java.util.Locale;
import java.util.ResourceBundle;

public class Main extends Application{
private static Scene scene;

//C195 Project by Christopher Simonetti Student 001219556

    @Override
    public void start(Stage stage) throws IOException {

        //Locale France = new Locale("fr", "FR");
        //Locale.setDefault(France);
        ResourceBundle rb = ResourceBundle.getBundle("util/rb",Locale.getDefault());
        scene = new Scene(loadFXML("UserLoginWindow",rb));
        stage.setScene(scene);
        stage.show();
    }


    private static Parent loadFXML(String fxml,ResourceBundle rb) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(UserLoginController.class.getResource(fxml + ".fxml"));
        fxmlLoader.setResources(rb);
        return fxmlLoader.load();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
