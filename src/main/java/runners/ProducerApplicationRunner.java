package runners;

import java.io.*;
import java.nio.CharBuffer;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.channels.FileChannel.MapMode;

public class ProducerApplicationRunner {

    private static final String SHARED_MEMORY_PATH = "/dev/shm/image-cache";
    private static final String RECEIVED_FILE = "./received.txt";
    private static final String SEND_FILE = "./send.txt";


    public void runCobolApp() throws FileNotFoundException {

        executeApp();

    }

    private void executeApp() throws FileNotFoundException {

        RandomAccessFile sharedMemory = new RandomAccessFile(SHARED_MEMORY_PATH, "rw");

        try (FileChannel channel = sharedMemory.getChannel()) {

            writeToSHM(channel, SHARED_MEMORY_PATH);

            readSHM(channel, SHARED_MEMORY_PATH);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void writeToSHM(FileChannel channel, String path) throws Exception {

        MappedByteBuffer buffer = channel.map(MapMode.READ_WRITE, 0, 609600);
        CharBuffer charBuffer = buffer.asCharBuffer();
        charBuffer.clear();

        charBuffer.put(getOutputChars(SEND_FILE));
        channel.close();
    }

    private void readSHM(FileChannel channel, String path) throws IOException {

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

        BufferedReader br = new BufferedReader(new FileReader(path));
        String line;
        StringBuilder builder = new StringBuilder();
        while ((line = br.readLine()) != null) {
            builder.append(line).append("\n");
        }
        br.close();

        return builder.toString().toCharArray();
    }

}
