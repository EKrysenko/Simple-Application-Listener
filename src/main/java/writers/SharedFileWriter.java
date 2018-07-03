package writers;

import java.io.*;

public class SharedFileWriter {

    public void writeToFile(String path, String text) throws Exception {

        try (Writer writer = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(path), "utf-8"))) {

            writer.write(text);
        }
    }

}
