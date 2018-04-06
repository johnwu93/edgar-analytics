import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.function.Consumer;

public class InputReader {

    private final String fileName;
    private final Consumer<String> consumeLine;

    InputReader(String fileName, Consumer<String> consumeLine) {
        this.fileName = fileName;
        this.consumeLine = consumeLine;
    }

    void processFile() throws IOException {
        BufferedReader bufferReader = new BufferedReader(new FileReader(fileName));
        bufferReader.readLine(); // read header
        String line;
        while((line = bufferReader.readLine()) != null) {
            consumeLine.accept(line);
        }
    }



}
