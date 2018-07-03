package readers;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

public class ConsoleReader {

    public static String readConsole() throws IOException {

        BufferedReader br = new BufferedReader(new InputStreamReader(System.in));

        return br.readLine();
    }

    public static String readConcole(Process process) {
        BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
        StringBuilder builder = new StringBuilder();
        String output = "";

        try {
            while (reader.readLine() != null) {
                builder.append(reader.readLine());
            }
            output = builder.toString();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return output;
    }

}
