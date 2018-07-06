package protocols;

import constants.Constants;
import interfaces.TransferProtocol;
import schedulers.Scheduler;

import java.io.*;
import java.nio.CharBuffer;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.channels.FileChannel.MapMode;

import static constants.Constants.*;

public class SHMtransferProtocol implements TransferProtocol {

    @Override
    public void executeProducer() {

        char[] charBuffer = getOutputChars(Constants.SEND_FILE);
        Scheduler scheduler = new Scheduler(NAMED_PIPE, charBuffer.length);

        try (RandomAccessFile sharedMemory = new RandomAccessFile(SHARED_MEMORY_PATH, "rw");
             FileChannel channel = sharedMemory.getChannel()) {

            long start = System.nanoTime();

            writeToSHM(channel, charBuffer);
            scheduler.sendMessage(0);

            if (scheduler.getCommand() == 1) {
                charBuffer = readFromSHM(channel, scheduler.getSizeCharArray());
            } else {
                charBuffer = "no data received".toCharArray();
            }

            long finish = System.nanoTime();

            writeReceivedToTextFile(charBuffer);
            System.out.println((finish - start) / 1e6);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void executeConsumer() {

        Scheduler scheduler = new Scheduler(NAMED_PIPE);

        if (scheduler.getCommand() == 0) {

            try (RandomAccessFile sharedMemory = new RandomAccessFile(SHARED_MEMORY_PATH, "rw");
                 FileChannel channel = sharedMemory.getChannel()) {

                char[] inputChars = readFromSHM(channel, scheduler.getSizeCharArray());

                // TODO: here we can add some logic to change input data before sending

                writeToSHM(channel, inputChars);

            } catch (Exception e) {
                e.printStackTrace();
            }

            scheduler.sendMessage(1);
        }
    }


    private void writeToSHM(FileChannel channel, char[] outputChars) throws Exception {

        MappedByteBuffer buffer = channel.map(MapMode.READ_WRITE, 0, SIZE);
        CharBuffer charBuffer = buffer.asCharBuffer();
        charBuffer.clear();

        charBuffer.put(outputChars);
    }

    private char[] readFromSHM(FileChannel channel, int sizeCharArray) throws IOException {

        MappedByteBuffer buffer = channel.map(MapMode.READ_WRITE, 0, SIZE);
        CharBuffer charBuf = buffer.asCharBuffer();
        char[] received = new char[sizeCharArray];

        charBuf.get(received);
        return received;
    }

    private void writeReceivedToTextFile(char[] received) {

        try (FileWriter writer = new FileWriter(RECEIVED_FILE)) {

            writer.write(received);
            writer.flush();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private char[] getOutputChars(String path) {

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
