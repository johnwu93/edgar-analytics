package UserSession;

import java.time.LocalDateTime;
import java.util.Objects;

public class UserSessionLogOutput {

    private final String userId;
    private final LocalDateTime firstRequestTime;
    private final LocalDateTime lastRequestTime;
    private final int numWebPageRequest;

    UserSessionLogOutput(String userId, LocalDateTime firstRequestTime, LocalDateTime lastRequestTime, int numWebPageRequest) {
        this.userId = userId;
        this.firstRequestTime = firstRequestTime;
        this.lastRequestTime = lastRequestTime;
        this.numWebPageRequest = numWebPageRequest;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        UserSessionLogOutput that = (UserSessionLogOutput) o;
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
