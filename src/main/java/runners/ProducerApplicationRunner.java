package runners;

import scheduler.Scheduler;

import java.io.*;
import java.nio.CharBuffer;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.channels.FileChannel.MapMode;

public class ProducerApplicationRunner {

    private static final String SHARED_MEMORY_PATH = "/dev/shm/image-cache";
    private static final String RECEIVED_FILE = "./received.txt";
    private static final String SEND_FILE = "./send.txt";
    private static final String NAMED_PIPE = "/home/egor/COBOL_WORKS/test_fifo/TEMP/FILE.in";

    public void runCobolApp() {

        executeApp();

    }

    private void executeApp() {

        Scheduler scheduler = new Scheduler(NAMED_PIPE);
        try (RandomAccessFile sharedMemory = new RandomAccessFile(SHARED_MEMORY_PATH, "rw");
             FileChannel channel = sharedMemory.getChannel()) {

            writeToSHM(channel);
            scheduler.setCommand(0);
            if (scheduler.getCommand() == 1) {
                readSHM(channel);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void writeToSHM(FileChannel channel) throws Exception {

        MappedByteBuffer buffer = channel.map(MapMode.READ_WRITE, 0, 609600);
        CharBuffer charBuffer = buffer.asCharBuffer();
        charBuffer.clear();

        charBuffer.put(getOutputChars(SEND_FILE));
    }

    private void readSHM(FileChannel channel) throws IOException {

        MappedByteBuffer buffer = channel.map(MapMode.READ_WRITE, 0, 609600);
        CharBuffer charBuf = buffer.asCharBuffer();
        char[] received = new char[4096];

        charBuf.get(received);

        writeReceivedToTextFile(received);
    }

    private void writeReceivedToTextFile(char[] received) {

        try(FileWriter writer = new FileWriter(RECEIVED_FILE)) {

            writer.write(received);
            writer.flush();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private char[] getOutputChars(String path) throws IOException {

        String line;
        StringBuilder builder = new StringBuilder();
        try (BufferedReader br = new BufferedReader(new FileReader(path))) {
            while ((line = br.readLine()) != null) {
                builder.append(line).append("\n");
            }
        }

        return builder.toString().toCharArray();
    }

}
