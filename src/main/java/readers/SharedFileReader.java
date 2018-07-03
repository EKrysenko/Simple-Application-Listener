package readers;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

public class SharedFileReader {

    public String readFile(String path) throws IOException {

        String result;
        try (BufferedReader br = new BufferedReader(new FileReader(path))) {

            StringBuilder sb = new StringBuilder();
            String line = br.readLine();

            while (line != null) {
                sb.append(line);
                sb.append(System.lineSeparator());
                line = br.readLine();
            }
            result = sb.toString();
        }
        return result;
    }
}