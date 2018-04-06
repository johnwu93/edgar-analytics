package WebLog;

import org.junit.Test;

import java.time.LocalDateTime;

import static org.junit.Assert.assertEquals;

public class WebLogTest {

    @Test
    public void createWebLog() {
        String webLogString = "101.81.133.jja,2017-06-30,00:00:00,0.0,1608552.0,0001047469-17-004337,-index.htm,200.0,80251.0,1.0,0.0,0.0,9.0,0.0,";
        LocalDateTime expectedDate = LocalDateTime.of(2017, 6, 30, 0, 0, 0);
        WebLog expectedWebLog = new WebLog("101.81.133.jja", expectedDate);
        assertEquals(expectedWebLog, WebLogFactory.createWebLog(webLogString));
    }
}
