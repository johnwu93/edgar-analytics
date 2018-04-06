import UserSession.UserSessionLogOutput;
import UserSession.UserSessionLogOutputFactory;
import WebLog.WebLog;

import java.time.LocalDateTime;

public class Util {
    static UserSessionLogOutput computeSingleRequestLog(WebLog webLog) {
        LocalDateTime date = webLog.getDate();
        return UserSessionLogOutputFactory.createLogOutput(webLog.getUserID(), date, date, 1);
    }
}
