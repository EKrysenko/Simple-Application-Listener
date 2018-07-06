package interfaces;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

public interface TransferProtocol {


    default void execute(String executor) {
        switch (executor) {
            case "producer":
                executeProducer();
                break;
            case "consumer":
                executeConsumer();
                break;
        }
    }

    void executeProducer();

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
