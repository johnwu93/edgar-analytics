package UserSession;

import java.time.LocalDateTime;

public final class UserSessionLogOutputFactory {
    public static UserSessionLogOutput createLogOutput(UserSession userSession) {
        return new UserSessionLogOutput(
                userSession.getUserId(),
                userSession.getFirstRequestTime(),
                userSession.getLastRequestTime(),
                userSession.getNumWebPageRequest()
        );
    }

    public static UserSessionLogOutput createLogOutput(String userId, LocalDateTime firstRequestTime, LocalDateTime lastRequestTime, int numWebPageRequest) {
        return new UserSessionLogOutput(
                userId,
                firstRequestTime,
                lastRequestTime,
                numWebPageRequest
        );
    }
}
