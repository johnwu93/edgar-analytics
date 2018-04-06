import UserSession.UserSessionLogOutput;

import java.io.FileWriter;
import java.io.IOError;
import java.io.IOException;

public class UserSessionReporterImpl implements UserSessionReporter {
    private final FileWriter fileWriter;

    public UserSessionReporterImpl(String writerName) {
        try {
            this.fileWriter = new FileWriter(writerName);
        } catch (IOException e) {
            throw new IOError(e);
        }

    }

    @Override
    public void report(UserSessionLogOutput log) {
        try {
            fileWriter.write(log.toString() + "\n");
        } catch (IOException e) {
            throw new IOError(e);
        }
    }


}
