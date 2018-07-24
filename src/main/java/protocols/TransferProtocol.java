package protocols;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

public interface TransferProtocol {

    default void execute(String executor, String lowSizePackage, String highSizePackage, String transferTimeInSeconds) {
        switch (executor) {
            case "producer":
                executeProducer(Integer.parseInt(lowSizePackage),
                        Integer.parseInt(highSizePackage),
                        Integer.parseInt(transferTimeInSeconds));
                break;
            case "consumer":
                executeConsumer();
                break;
        }
    }

    void executeProducer(int lowSizePackage, int highSizePackage, int transferTimeInSeconds);

    void executeConsumer();

    default void writeToFile(String path, char[] received) {

        try (FileWriter writer = new FileWriter(path)) {

            writer.write(received);
            writer.flush();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    default char[] readFile(String path) {

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
