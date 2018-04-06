import UserSession.UserSession;
import WebLog.WebLog;
import org.apache.commons.collections4.map.LinkedMap;

import java.time.LocalDateTime;

import static UserSession.UserSessionLogOutputFactory.createLogOutput;

final class WebLogConsumer {
    private int inactivityPeriod;
    private final UserSessionReporter reporter;
    private final LinkedMap<String, UserSession> userSessionSet;

    WebLogConsumer(UserSessionReporter reporter, int inactivityPeriod) {
        this.reporter = reporter;
        this.inactivityPeriod = inactivityPeriod;
        this.userSessionSet = new LinkedMap<>();
    }

    // invariant: assumes that each the time for each input for webLog will be greater than the previous one
    // calculates all the current sessions for a user
    // logs sessions
    void processRequest(WebLog webLog) {
        String userID = webLog.getUserID();
        LocalDateTime date = webLog.getDate();

        checkInactivity(date);

        if (!userSessionSet.containsKey(userID)) {
            UserSession newUserSession = new UserSession(userID, date, date, 1, inactivityPeriod);
            userSessionSet.put(userID, newUserSession);
        } else {
            UserSession oldLog = userSessionSet.remove(userID);
            UserSession newUserSession = new UserSession(userID, oldLog.getFirstRequestTime(), date, oldLog.getNumWebPageRequest() + 1, inactivityPeriod);
            userSessionSet.put(userID, newUserSession);
        }
    }

    public void checkInactivity(LocalDateTime date) {
        while (!userSessionSet.isEmpty() && userSessionSet.get(userSessionSet.firstKey()).isExpired(date)) {
            UserSession log = userSessionSet.remove(userSessionSet.firstKey());
            reporter.report(createLogOutput(log));
        }
    }

    public void close() {
        userSessionSet.forEach((key, log) -> reporter.report(createLogOutput(log)));
    }
}
