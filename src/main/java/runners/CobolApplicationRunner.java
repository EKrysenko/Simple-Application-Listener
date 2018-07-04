package runners;

import readers.SharedFileReader;
import writers.SharedFileWriter;

public class CobolApplicationRunner {

    private static final String INPUT_PATH = "/dev/shm/image-cache";
    private static final String OUTPUT_PATH = "/dev/shm/image-cache";

    public static void runCobolApp() {

        executeApp();

    }

    private static void executeApp() {

        try {

            byte[] bytes = new SharedFileReader().readFile(OUTPUT_PATH);

            new SharedFileWriter().writeToFile(INPUT_PATH, bytes);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
