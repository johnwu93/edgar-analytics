import UserSession.UserSession;
import WebLog.WebLog;

import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.List;

import static UserSession.UserSessionLogOutputFactory.createLogOutput;

final class WebLogConsumer {
    private final UserSessionSet userSessionSet;
    private final int inactivityPeriod;
    private final UserSessionReporter reporter;
    private int processIndex; // helps sorted out ids when the program ends

    WebLogConsumer(UserSessionReporter reporter, int inactivityPeriod) {
        this.reporter = reporter;
        this.inactivityPeriod = inactivityPeriod;
        this.userSessionSet = new UserSessionSet();
        this.processIndex = 0;
    }

    // invariant: assumes that each the time for each input for webLog will be greater than the previous one
    // calculates all the current sessions for a user
    // logs sessions
    void processRequest(WebLog webLog) {
        String userID = webLog.getUserID();
        LocalDateTime date = webLog.getDate();

        checkInactivity(date);

        if (!userSessionSet.containsSession(userID)) {
            userSessionSet.addSession(new UserSession(userID, date, date, 1, inactivityPeriod, this.processIndex));
        } else {
            userSessionSet.updateSession(userID, date);
        }
        this.processIndex++;
    }

    public void checkInactivity(LocalDateTime date) {
        final List<UserSession> inactiveSessions = userSessionSet.removeInactiveSessions(date);
        for (UserSession session : inactiveSessions) {
            reporter.report(createLogOutput(session));
        }
    }

    public void close() {
        final List<UserSession> userSessions = userSessionSet.getSessions();
        userSessions.sort(Comparator.comparingInt(UserSession::getFirstCreatedId));
        userSessions.forEach(log -> reporter.report(createLogOutput(log)));
    }
}
