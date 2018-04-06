import WebLog.WebLog;

import java.time.LocalDateTime;

final class WebLogConsumer {
    private int inactivityPeriod;
    private final UserSessionReporter reporter;

    WebLogConsumer(UserSessionReporter reporter, int inactivityPeriod) {
        this.reporter = reporter;
        this.inactivityPeriod = inactivityPeriod;
    }

    // invariant: assumes that each the time for each input for webLog will be greater than the previous one
    // calculates all the current sessions for a user
    // logs sessions
    void processRequest(WebLog webLog) {
        String userID = webLog.getUserID();
        LocalDateTime date = webLog.getDate();
        UserSessionLog userSessionLog = new UserSessionLog(userID, date, date.plusSeconds(inactivityPeriod), 1);
        reporter.report(userSessionLog);
    }
}
