package Model;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;

public class Logger {

    private static final String fileName = "login_attempts.txt";

    public void  appendFile(String userName, Boolean success)
    {
        String attempt = "";
        if(!success)
            attempt = "Login Failed";
        else
            attempt = "Login Successful";

        try {
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MM/dd/yyyy - HH:mm:ss Z");
            ZonedDateTime zonedDateTimeNow = ZonedDateTime.now(ZoneId.of("UTC"));
            String formattedString = zonedDateTimeNow.format(formatter);
            BufferedWriter logger = new BufferedWriter(new FileWriter(fileName, true));
            logger.append(formattedString + " UTC -- Login Attempt By: " + userName + " -- "+ attempt + "\n");
            logger.flush();
            logger.close();
        }
        catch (IOException error) {
            error.printStackTrace();
        }
    }

}
