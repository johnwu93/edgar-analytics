import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOError;
import java.io.IOException;

import static WebLog.WebLogFactory.createWebLog;

public class Main {
    public static void main(String[] arv) {
        final String directory = "insight_testsuite/test_1/";
        UserSessionReporter fileReporter = new UserSessionReporterImpl(directory + "output/myoutput.txt");
        int inactivityPeriod = 0;
        try {
            inactivityPeriod = Integer.parseInt(new BufferedReader(new FileReader("input/inactivity_period.txt")).readLine());
        } catch (IOException e) {
            throw new IOError(e);
        }
        WebLogConsumer consumer = new WebLogConsumer(fileReporter, inactivityPeriod);
        InputReader inputReader = new InputReader(
                directory + "input/log.csv",
                (inputLine) -> consumer.processRequest(createWebLog(inputLine))
        );

        try {
            inputReader.processFile();
        } catch (IOException e) {
            throw new IOError(e);
        }
    }
}
