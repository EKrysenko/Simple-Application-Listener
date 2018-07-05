package runners;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.CharBuffer;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;

import static java.nio.channels.FileChannel.MapMode;

public class ConsumerApplicationRunner {

    private static final String PATH = "/dev/shm/image-cache";
    private static final String MODE = "rw";


    public void runCobolApp() throws FileNotFoundException {

        executeApp();

    }

    private void executeApp() throws FileNotFoundException {

        RandomAccessFile sharedMemory = new RandomAccessFile(PATH, MODE);

        try (FileChannel channel = sharedMemory.getChannel()) {

            char[] inputChars = readFromSHM(channel);

            // TODO: here we can add some logic to change input data before sending

            writeToSHM(channel, inputChars);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    private char[] readFromSHM(FileChannel channel) throws IOException {

        MappedByteBuffer buffer = channel.map(MapMode.READ_WRITE, 0, 609600);
        CharBuffer charBuf = buffer.asCharBuffer();
        char[] received = new char[4096];

        charBuf.get(received);

        return received;
    }

    private void writeToSHM(FileChannel channel, char[] outputChars) throws Exception {


        MappedByteBuffer buffer = channel.map(MapMode.READ_WRITE, 0, 609600);
        CharBuffer charBuffer = buffer.asCharBuffer();
        charBuffer.clear();

        charBuffer.put(outputChars);
    }
}
