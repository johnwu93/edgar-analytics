package WebLog;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class WebLogFactory {

    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    public static WebLog createWebLog(String weblogLine) {
        String[] inputArguments = weblogLine.split(",", 4);
        String userId = inputArguments[0];
        String dateString = inputArguments[1] + " " + inputArguments[2];
        return new WebLog(userId, LocalDateTime.parse(dateString, DATE_FORMATTER));
    }
}
