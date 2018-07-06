package runners;

import scheduler.Scheduler;

import java.io.*;
import java.nio.CharBuffer;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.channels.FileChannel.MapMode;

public class ProducerApplicationRunner {

    private static final int SIZE = 748835;
    private static final String SHARED_MEMORY_PATH = "/dev/shm/image-cache";
    private static final String RECEIVED_FILE = "./received.txt";
    private static final String SEND_FILE = "./send.txt";
    private static final String NAMED_PIPE = "/home/egor/COBOL_WORKS/LGI-HRWD/work_dir/FILE.in";

    public void runCobolApp() {

        executeApp();

    }

    private void executeApp() {

        char[] charBuffer = getOutputChars(SEND_FILE);
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
            System.out.println("elapsed time is " + (finish - start) / 1e6 + " ms");
        } catch (Exception e) {
            e.printStackTrace();
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
