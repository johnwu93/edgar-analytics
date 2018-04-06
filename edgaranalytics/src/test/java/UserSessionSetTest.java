import UserSession.UserSession;
import org.junit.Assert;
import org.junit.Test;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;

public class UserSessionSetTest {

    private static final LocalDateTime INITIAL_TIME = LocalDateTime.of(2017, 1, 1, 0, 0, 0);
    private static final int INACTIVITY_PERIOD = 2;

    @Test
    public void add1Session() {
        UserSessionSet userSessionSet = new UserSessionSet();

        userSessionSet.addSession(createSingleSession("foo", 0));

        assertEqualSessions(userSessionSet, createSingleSession("foo", 0));
    }

    @Test
    public void add2Sessions() {
        UserSessionSet userSessionSet = new UserSessionSet();
        userSessionSet.addSession(createSingleSession("foo", 0));
        userSessionSet.addSession(createSingleSession("bar", 1));

        assertEqualSessions(userSessionSet, createSingleSession("foo", 0), createSingleSession("bar", 1));
    }

    @Test
    public void add2SessionsUpdatesEarliest() {
        UserSessionSet userSessionSet = new UserSessionSet();

        userSessionSet.addSession(createSingleSession("foo", 0));
        userSessionSet.addSession(createSingleSession("bar", 1));
        userSessionSet.updateSession("foo", INITIAL_TIME.plusSeconds(2));

        assertEqualSessions(
                userSessionSet,
                createSingleSession("bar", 1),
                new UserSession("foo", INITIAL_TIME.plusSeconds(0), INITIAL_TIME.plusSeconds(2), 2, 2)
        );
    }

    @Test
    public void noInactiveSessions() {
        UserSessionSet userSessionSet = new UserSessionSet();
        userSessionSet.addSession(createSingleSession("foo", 3));
        userSessionSet.addSession(createSingleSession("bar", 5));
        List<UserSession> inactiveSessions = userSessionSet.removeInactiveSessions(INITIAL_TIME);

        Assert.assertTrue(Collections.emptyList().equals(inactiveSessions));
    }

    @Test
    public void oneInactiveSession() {
        UserSessionSet userSessionSet = new UserSessionSet();
        final UserSession inactiveSession = createSingleSession("foo", 2);
        userSessionSet.addSession(inactiveSession);
        userSessionSet.addSession(createSingleSession("bar", 2 + 2 * INACTIVITY_PERIOD));
        List<UserSession> inactiveSessions = userSessionSet.removeInactiveSessions(INITIAL_TIME.plusSeconds(2 + INACTIVITY_PERIOD + 1));
        final List<UserSession> expectedInactiveSessions = Collections.singletonList(inactiveSession);
        Assert.assertTrue(expectedInactiveSessions.equals(inactiveSessions));
    }

    @Test
    public void multipleInactiveSessions() {
        UserSessionSet userSessionSet = new UserSessionSet();
        final List<UserSession> expectedInactiveSessions = List.of(createSingleSession("foo", 2), createSingleSession("bar", 4));
        for (UserSession inactiveSession : expectedInactiveSessions) {
            userSessionSet.addSession(inactiveSession);
        }
        userSessionSet.addSession(createSingleSession("baz", 20));

        List<UserSession> inactiveSessions = userSessionSet.removeInactiveSessions(INITIAL_TIME.plusSeconds(10));
        Assert.assertTrue(expectedInactiveSessions.equals(inactiveSessions));
    }

    private void assertEqualSessions(UserSessionSet sessionSet, UserSession... expectedSessions) {
        for (UserSession expectedSession : expectedSessions) {
            Assert.assertEquals(expectedSession, sessionSet.popLastUpdatedSession());
        }
    }

    private static UserSession createSingleSession(String userId, int time) {
        return new UserSession(
                userId,
                INITIAL_TIME.plusSeconds(time),
                INITIAL_TIME.plusSeconds(time),
                1,
                INACTIVITY_PERIOD
        );
    }
}
