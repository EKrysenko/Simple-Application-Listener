package protocols;

import interfaces.TransferProtocol;
import schedulers.Scheduler;

import static constants.Constants.*;

public class HDDtransferProtocol implements TransferProtocol {

    @Override
     public void executeProducer() {

        Scheduler scheduler = new Scheduler(NAMED_PIPE);

        char[] bufferChars = readFile(SEND_FILE).toCharArray();

        long start = System.nanoTime();

        writeToFile(HDD_PATH, bufferChars);

        scheduler.sendMessage(0);

        if (scheduler.getCommand() == 1) {
            bufferChars = readFile(HDD_PATH).toCharArray();
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


            char[] inputChars = readFile(HDD_PATH).toCharArray();

            // TODO: here we can add some logic to change input data before sending

            writeToFile(HDD_PATH, inputChars);

            scheduler.sendMessage(1);
        }
    }



}