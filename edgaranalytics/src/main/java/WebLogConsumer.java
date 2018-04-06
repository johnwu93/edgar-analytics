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

        while (!userSessionSet.isEmpty() && userSessionSet.get(userSessionSet.lastKey()).isExpired(date)) {
            UserSession log = userSessionSet.remove(userSessionSet.lastKey());
            reporter.report(createLogOutput(log));
        }

        UserSession newLog = userSessionSet.compute(userID, (id, userSession) -> {
            int numRequest = userSession == null ? 1 : userSession.getNumWebPageRequest() + 1;
            return new UserSession(userID, date, date.plusSeconds(inactivityPeriod), numRequest, inactivityPeriod);
        });
        userSessionSet.put(userID, newLog);
    }

    public void close() {
        userSessionSet.forEach((key, log) -> reporter.report(createLogOutput(log)));
    }
}
