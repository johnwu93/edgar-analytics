import UserSession.UserSessionLogOutput;
import UserSession.UserSessionLogOutputFactory;
import WebLog.WebLog;
import WebLog.WebLogFactory;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class WebLogConsumerTest {

    private List<UserSessionLogOutput> sessionLogs;
    private static final LocalDateTime INITIAL_TIME = LocalDateTime.of(2017, 1, 1, 0, 0, 0);
    private UserSessionReporter reporter = (log) -> sessionLogs.add(log);

    @Before
    public void setUp() {
        sessionLogs = new ArrayList<>();
    }

    @Test
    public void processSingleRequest() {
        WebLogConsumer webLogConsumer = new WebLogConsumer(reporter, 2);
        createAndProcessRequest(webLogConsumer, 0, "foo");
        webLogConsumer.close();

        assertEqualSessions(createLogOutput("foo", 0, 0, 1));
    }

    @Test
    public void processTwoRequestsFromASingleUserCreateTwoSessions() {
        int inactivityPeriod = 2;
        WebLogConsumer webLogConsumer = new WebLogConsumer(reporter, inactivityPeriod);
        int secondRequestDelay = 5;
        LocalDateTime secondRequestTime = INITIAL_TIME.plusSeconds(secondRequestDelay);

        String userId = "foo";
        WebLog fooWebLog1 = WebLogFactory.createWebLog(userId, INITIAL_TIME);
        webLogConsumer.processRequest(fooWebLog1);

        WebLog fooWebLog2 = WebLogFactory.createWebLog(userId, secondRequestTime);
        webLogConsumer.processRequest(fooWebLog2);
        webLogConsumer.close();

        assertEqualSessions(
                Util.computeSingleRequestLog(fooWebLog1),
                Util.computeSingleRequestLog(fooWebLog2)
        );
    }

    @Test
    public void processTwoRequestsSingleSessionMultipleRequest() {
        int inactivityPeriod = 2;
        WebLogConsumer webLogConsumer = new WebLogConsumer(reporter, inactivityPeriod);
        int secondRequestDelay = 1;

        String userId = "foo";
        createAndProcessRequest(webLogConsumer, 0, userId);
        createAndProcessRequest(webLogConsumer, secondRequestDelay, userId);
        webLogConsumer.close();

        assertEqualSessions(
                createLogOutput(userId, 0, secondRequestDelay, 2)
        );
    }

    @Test
    public void processTwoRequestsThatOccurredSimultaneouslyByDifferentUsers() {
        int inactivityPeriod = 2;
        WebLogConsumer webLogConsumer = new WebLogConsumer(reporter, inactivityPeriod);

        WebLog fooWebLog = WebLogFactory.createWebLog("foo", INITIAL_TIME);
        webLogConsumer.processRequest(fooWebLog);
        WebLog barWebLog = WebLogFactory.createWebLog("bar", INITIAL_TIME);
        webLogConsumer.processRequest(barWebLog);
        webLogConsumer.close();

        assertEqualSessions(
                Util.computeSingleRequestLog(fooWebLog),
                Util.computeSingleRequestLog(barWebLog)
        );
    }

    @Test
    public void processOnlyOneRequest() {
        int inactivityPeriod = 2;
        WebLogConsumer webLogConsumer = new WebLogConsumer(reporter, inactivityPeriod);

        createAndProcessRequest(webLogConsumer, 0, "jja", "jfd");
        createAndProcessRequest(webLogConsumer, 1, "jfd", "hbc");
        createAndProcessRequest(webLogConsumer, 2, "jie");

        webLogConsumer.checkInactivity(INITIAL_TIME.plusSeconds(3));

        assertEqualSessions(
                createLogOutput("jja", 0, 0, 1)
        );
    }

    @Test
    public void processTwoRequestFromAUserWhereARequestIsMadeWhenASessionWasAboutToEnd() {
        WebLogConsumer webLogConsumer = new WebLogConsumer(reporter, 2);

        String userId = "jfd";
        createAndProcessRequest(webLogConsumer, 0, userId, userId);
        createAndProcessRequest(webLogConsumer, 1, userId);
        createAndProcessRequest(webLogConsumer, 3, userId);

        webLogConsumer.checkInactivity(INITIAL_TIME.plusSeconds(4));

        assertEqualSessions();
    }

    @Ignore("When the dashboard closes, it processes the remaining getSessions by when they arrived")
    @Test
    public void processReadmeExample() {
        WebLogConsumer webLogConsumer = new WebLogConsumer(reporter, 2);
        String jja = "jja";
        String jfd = "jfd";
        String hbc = "hbc";
        String jie = "jie";
        String aag = "aag";
        createAndProcessRequest(webLogConsumer, 0, jja, jfd, jfd);
        createAndProcessRequest(webLogConsumer, 1, jfd, hbc);
        createAndProcessRequest(webLogConsumer, 2, jie, aag);
        createAndProcessRequest(webLogConsumer, 3, jfd);
        createAndProcessRequest(webLogConsumer, 4, hbc, aag);
        webLogConsumer.close();

        assertEqualSessions(
                createLogOutput(jja, 0, 0, 1),
                createLogOutput(hbc, 1, 1, 1),
                createLogOutput(jfd, 0, 3, 4),
                createLogOutput(jie, 2, 2, 1),
                createLogOutput(aag, 2, 4, 2),
                createLogOutput(hbc, 4, 4, 1)
        );
    }

    private static void createAndProcessRequest(WebLogConsumer webLogConsumer, int timeReference, String... userIds) {
        for (String userId : userIds) {
            WebLog fooWebLog1 = WebLogFactory.createWebLog(userId, INITIAL_TIME.plusSeconds(timeReference));
            webLogConsumer.processRequest(fooWebLog1);
        }
    }

    private static UserSessionLogOutput createLogOutput(String userId, int relativeStartTime, int relativeEndTime, int numCounts) {
        return UserSessionLogOutputFactory.createLogOutput(
                userId,
                INITIAL_TIME.plusSeconds(relativeStartTime),
                INITIAL_TIME.plusSeconds(relativeEndTime),
                numCounts
        );
    }

    private void assertEqualSessions(UserSessionLogOutput... expected) { // todo check assertequals
        Assert.assertTrue(Arrays.asList(expected).equals(sessionLogs));
    }
}
