package schedulers;


import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.IntBuffer;
import java.nio.channels.FileChannel;

public class SHMscheduler implements Scheduler {

    private final int MESSAGE_SIZE = 20;

    private String path;
    private int command;
    private int sizeCharArray;
    private int batchSize;
    private int offSet;
    private int currentBatch;

    public SHMscheduler(String path, int sizeCharArray, int batchSize, int offSet) {
        this.path = path;
        this.offSet = offSet;
        this.sizeCharArray = sizeCharArray;
        this.batchSize = batchSize;
    }

    @Override
    public int getMessage() {
        int[] message = new int[4];

        getIntBuffer().get(message);

        sizeCharArray = message[0];
        batchSize = message[1];
        currentBatch = message[2];
        command = message[3];

        return command;
    }

    public void sendMessage(int sizeCharArray, int batchSize, int currentBatch, int command){
        getIntBuffer().put(new int[] {sizeCharArray, batchSize, currentBatch, command});
    }

    @Override
    public void sendMessage(int command) {

    }

    @Override
    public int getSizeCharArray() {
        return sizeCharArray;
    }

    private IntBuffer getIntBuffer() {
        IntBuffer buffer = null;

        try (RandomAccessFile sharedMemory = new RandomAccessFile(path, "rw");
             FileChannel channel = sharedMemory.getChannel()) {

            buffer = channel.map(FileChannel.MapMode.READ_WRITE, offSet, MESSAGE_SIZE).asIntBuffer();

        } catch (IOException e) {
            e.printStackTrace();
        }
        return buffer;
    }
}
