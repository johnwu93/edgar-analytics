package UserSession;

import java.time.LocalDateTime;
import java.util.Objects;

public final class UserSession {

    private final String userId;
    private final LocalDateTime firstRequestTime;
    private final LocalDateTime lastRequestTime;
    private final int numWebPageRequest;
    private int inactivityPeriod;

    public String getUserId() {
        return userId;
    }

    public LocalDateTime getFirstRequestTime() {
        return firstRequestTime;
    }

    public LocalDateTime getLastRequestTime() {
        return lastRequestTime;
    }

    public int getNumWebPageRequest() {
        return numWebPageRequest;
    }

    private LocalDateTime getSessionExpiration() {
        return lastRequestTime.plusSeconds(inactivityPeriod);
    }

    public boolean isExpired(LocalDateTime latestTransactionTime) {
        return getSessionExpiration().compareTo(latestTransactionTime) < 0;
    }

    public UserSession(String userId, LocalDateTime firstRequestTime, LocalDateTime lastRequestTime, int numWebPageRequest, int inactivityPeriod) {
        this.userId = userId;
        this.firstRequestTime = firstRequestTime;
        this.lastRequestTime = lastRequestTime;
        this.numWebPageRequest = numWebPageRequest;
        this.inactivityPeriod = inactivityPeriod;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        UserSession that = (UserSession) o;
        return numWebPageRequest == that.numWebPageRequest &&
                Objects.equals(userId, that.userId) &&
                Objects.equals(firstRequestTime, that.firstRequestTime) &&
                Objects.equals(lastRequestTime, that.lastRequestTime);
    }

    @Override
    public int hashCode() {
        return Objects.hash(userId, firstRequestTime, lastRequestTime, numWebPageRequest);
    }
}
