package WebLog;

import java.time.LocalDateTime;
import java.util.Objects;

final class WebLog {
    // Did not include cik, accession and extention because it was not needed for this challenge
    private final String userID;
    private final LocalDateTime date;

    WebLog(String userID, LocalDateTime date) {
        this.userID = userID;
        this.date = date;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        WebLog webLog = (WebLog) o;
        return Objects.equals(userID, webLog.userID) &&
                Objects.equals(date, webLog.date);
    }

    @Override
    public int hashCode() {
        return Objects.hash(userID, date);
    }
}
