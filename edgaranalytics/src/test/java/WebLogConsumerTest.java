import UserSession.UserSessionLogOutput;
import WebLog.WebLog;
import WebLog.WebLogFactory;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static UserSession.UserSessionLogOutputFactory.createLogOutput;

public class WebLogConsumerTest {

    private List<UserSessionLogOutput> sessionLogs;
    private static final LocalDateTime INITIAL_TIME = LocalDateTime.of(2017, 1, 1, 0, 0, 0);

    @Before
    public void setUp() {
        sessionLogs = new ArrayList<>();
    }

    private UserSessionReporter reporter = (log) -> sessionLogs.add(log);

    @Test
    public void processSingleRequest() {
        WebLogConsumer webLogConsumer = new WebLogConsumer(reporter, 2);
        WebLog fooWebLog = WebLogFactory.createWebLog("foo", INITIAL_TIME);
        webLogConsumer.processRequest(fooWebLog);
        webLogConsumer.close();

        assertEqualSessions(createLogOutput("foo", INITIAL_TIME, INITIAL_TIME.plusSeconds(2), 1));
    }

    @Test
    public void processTwoRequestsFromTwoUsers() {
        int inactivityPeriod = 2;
        WebLogConsumer webLogConsumer = new WebLogConsumer(reporter, inactivityPeriod);
        WebLog fooWebLog = WebLogFactory.createWebLog("foo", INITIAL_TIME);
        webLogConsumer.processRequest(fooWebLog);
        WebLog barWebLog = WebLogFactory.createWebLog("bar", INITIAL_TIME);
        webLogConsumer.processRequest(barWebLog);

        webLogConsumer.close();
        assertEqualSessions(
                computeLogWithOneCount(fooWebLog, inactivityPeriod),
                computeLogWithOneCount(barWebLog, inactivityPeriod)
        );
    }

    @Test
    public void processTwoRequestsFromASingleUserCreateTwoSessions() {
        int inactivityPeriod = 2;
        WebLogConsumer webLogConsumer = new WebLogConsumer(reporter, inactivityPeriod);
        int secondRequestDelay = 5;
        LocalDateTime secondRequestTime = INITIAL_TIME.plusSeconds(secondRequestDelay);

        WebLog fooWebLog1 = WebLogFactory.createWebLog("foo", INITIAL_TIME);
        webLogConsumer.processRequest(fooWebLog1);
        WebLog fooWebLog2 = WebLogFactory.createWebLog("foo", secondRequestTime);
        webLogConsumer.processRequest(fooWebLog2);
        webLogConsumer.close();

        assertEqualSessions(
                computeLogWithOneCount(fooWebLog1, inactivityPeriod),
                computeLogWithOneCount(fooWebLog2, inactivityPeriod)
        );
    }

    @Test
    public void processTwoRequestsSingleSessionMultipleRequest() {
        int inactivityPeriod = 2;
        WebLogConsumer webLogConsumer = new WebLogConsumer(reporter, inactivityPeriod);
        int secondRequestDelay = 1;
        LocalDateTime secondRequestTime = INITIAL_TIME.plusSeconds(secondRequestDelay);

        WebLog fooWebLog1 = WebLogFactory.createWebLog("foo", INITIAL_TIME);
        webLogConsumer.processRequest(fooWebLog1);
        WebLog fooWebLog2 = WebLogFactory.createWebLog("foo", secondRequestTime);
        webLogConsumer.processRequest(fooWebLog2);
        webLogConsumer.close();

        assertEqualSessions(
                createLogOutput("foo", secondRequestTime, secondRequestTime.plusSeconds(inactivityPeriod), 2)
        );
    }

    private UserSessionLogOutput computeLogWithOneCount(WebLog webLog, int inactivityPeriod) {
        LocalDateTime date = webLog.getDate();
        return createLogOutput(webLog.getUserID(), date, date.plusSeconds(inactivityPeriod), 1);
    }

    private void assertEqualSessions(UserSessionLogOutput... expected) { // todo check assertequals
        Assert.assertTrue(Arrays.asList(expected).equals(sessionLogs));
    }
}
