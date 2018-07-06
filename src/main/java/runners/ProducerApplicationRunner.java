package runners;

import scheduler.Scheduler;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

public class ProducerApplicationRunner {

    private static final String HDD_PATH = "/home/uliana/Documents/lgi/docker/benchmarks/results/buffertext.txt";
    private static final String RECEIVED_FILE = "/home/uliana/Documents/lgi/docker/benchmarks/results/received.txt";
    private static final String SEND_FILE = "/home/uliana/Documents/lgi/docker/benchmarks/results/send.txt";
    private static final String NAMED_PIPE = "/home/uliana/Documents/lgi/docker/FILE.in";

    public void runCobolApp() {

        executeApp();

    }

    private void executeApp() {

        Scheduler scheduler = new Scheduler(NAMED_PIPE);

        char[] bufferChars = readFile(SEND_FILE);

        long start = System.nanoTime();

        writeToFile(HDD_PATH, bufferChars);

        scheduler.setCommand(0);

        if (scheduler.getCommand() == 1) {
            bufferChars = readFile(HDD_PATH);
        } else {
            bufferChars = "no data received".toCharArray();
        }

        long finish = System.nanoTime();
        writeToFile(RECEIVED_FILE, bufferChars);

        System.out.println((finish - start) / 1e6);
    }


    private void writeToFile(String path, char[] outputChars) {

        try (FileWriter writer = new FileWriter(path)) {

            writer.write(outputChars);
            writer.flush();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private char[] readFile(String path) {

        String line;
        StringBuilder builder = new StringBuilder();
        try (BufferedReader br = new BufferedReader(new FileReader(path))) {
            while ((line = br.readLine()) != null) {
                builder.append(line).append("\n");
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        return builder.toString().toCharArray();
    }

}
