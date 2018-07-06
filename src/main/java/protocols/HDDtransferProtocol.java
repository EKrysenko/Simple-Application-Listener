package protocols;

import interfaces.TransferProtocol;
import schedulers.Scheduler;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

import static constants.Constants.*;

public class HDDtransferProtocol implements TransferProtocol {

    @Override
     public void executeProducer() {

        Scheduler scheduler = new Scheduler(NAMED_PIPE);

        char[] bufferChars = readFile(SEND_FILE);

        long start = System.nanoTime();

        writeToFile(HDD_PATH, bufferChars);

        scheduler.sendMessage(0);

        if (scheduler.getCommand() == 1) {
            bufferChars = readFile(HDD_PATH);
        } else {
            bufferChars = "no data received".toCharArray();
        }

        long finish = System.nanoTime();
        writeToFile(RECEIVED_FILE, bufferChars);

        System.out.println((finish - start) / 1e6);

    }

    @Override
    public void executeConsumer() {
        Scheduler scheduler = new Scheduler(NAMED_PIPE);

        if (scheduler.getCommand() == 0) {


            char[] inputChars = readFile(HDD_PATH);

            // TODO: here we can add some logic to change input data before sending

            writeToFile(HDD_PATH, inputChars);

            scheduler.sendMessage(1);
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

    private void writeToFile(String path, char[] outputChars) {

        try (FileWriter writer = new FileWriter(path)) {

            writer.write(outputChars);
            writer.flush();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}