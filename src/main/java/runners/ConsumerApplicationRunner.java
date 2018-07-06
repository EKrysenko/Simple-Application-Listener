package runners;

import scheduler.Scheduler;

import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.CharBuffer;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;

import static java.nio.channels.FileChannel.MapMode;

public class ConsumerApplicationRunner {

    private static final String PATH = "/dev/shm/image-cache";
    private static final String MODE = "rw";
    private static final String NAMED_PIPE = "/home/uliana/Documents/lgi/docker/FILE.in";
    private static final int SIZE = 500000000;

    public void runCobolApp() {

        executeApp();

    }

    private void executeApp() {

        Scheduler scheduler = new Scheduler(NAMED_PIPE);

        //System.out.println("before reading from pipe");

        if (scheduler.getCommand() == 0) {

            try (RandomAccessFile sharedMemory = new RandomAccessFile(PATH, MODE);
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


    private char[] readFromSHM(FileChannel channel, int sizeCharArray) throws IOException {

        MappedByteBuffer buffer = channel.map(MapMode.READ_WRITE, 0, SIZE);
        CharBuffer charBuf = buffer.asCharBuffer();
        char[] received = new char[sizeCharArray];

        charBuf.get(received);
        return received;
    }

    private void writeToSHM(FileChannel channel, char[] outputChars) throws Exception {

        MappedByteBuffer buffer = channel.map(MapMode.READ_WRITE, 0, SIZE);
        CharBuffer charBuffer = buffer.asCharBuffer();
        charBuffer.clear();

        charBuffer.put(outputChars);
    }
}
