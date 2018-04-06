import UserSession.UserSession;
import org.apache.commons.collections4.map.LinkedMap;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class UserSessionSet {
    private final LinkedMap<String, UserSession> sessionSet;

    UserSessionSet() {
        this.sessionSet = new LinkedMap<>();
    }

    void addSession(UserSession userSession) {
        sessionSet.put(userSession.getUserId(), userSession);
    }

    void updateSession(String userID, LocalDateTime newDate) {
        UserSession oldLog = sessionSet.remove(userID);
        UserSession newUserSession = new UserSession(userID, oldLog.getFirstRequestTime(), newDate, oldLog.getNumWebPageRequest() + 1, oldLog.getInactivityPeriod());
        sessionSet.put(userID, newUserSession);
    }

    private UserSession getLastUpdatedSession() {
        String userID = sessionSet.firstKey();
        return sessionSet.get(userID);
    }

    boolean containsSession(String userID) {
        return this.sessionSet.containsKey(userID);
    }

    UserSession popLastUpdatedSession() {
        String userID = sessionSet.firstKey();
        UserSession userSession = sessionSet.get(userID);
        sessionSet.remove(userID);
        return userSession;
    }

    public List<UserSession> removeInactiveSessions(LocalDateTime expiredTime) {
        List<UserSession> inactiveSessions = new ArrayList<>();
        if (!sessionSet.isEmpty()) {
            UserSession lastUpdatedSession = getLastUpdatedSession();
            if (lastUpdatedSession.isExpired(expiredTime)) {
                sessionSet.remove(lastUpdatedSession.getUserId());
                inactiveSessions.add(lastUpdatedSession);
                inactiveSessions.addAll(removeInactiveSessions(expiredTime));
            }
        }
        return inactiveSessions;
    }

    public Collection<UserSession> getSessions() {
        return sessionSet.values();
    }

}
