package protocols;

import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.channels.FileLock;
import java.util.function.Consumer;

import static constants.Constants.SHARED_MEMORY_PATH;
import static constants.Constants.SIZE;
import static java.nio.channels.FileChannel.MapMode.READ_WRITE;

public class SHMTransferProtocol {

    protected static final int CLEAR_UTIL = -1;
    protected static final int DATA_AREA_START = 64;
    protected static final int CONSUMER_OFFSET = 32;
    protected static final int PRODUCER_OFFSET = 0;

    protected MappedByteBuffer consumerUtil;
    protected MappedByteBuffer producerUtil;
    protected MappedByteBuffer producerData;
    protected MappedByteBuffer consumerData;

    protected String readData(FileChannel channel, int dataSize, int startIndex, MappedByteBuffer dataArea) throws IOException {
        FileLock dataAreaLock = channel.lock(startIndex, dataSize, true);
        char[] chars = new char[dataSize];
        dataArea.asCharBuffer().get(chars);
        String data = new String(chars);
        dataAreaLock.release();
        return data;
    }

    protected void writeData(String data, FileChannel channel, int utilAreaStart, int dataAreaStart, MappedByteBuffer utilArea, MappedByteBuffer dataArea) throws IOException {
        FileLock utilAreaLock = channel.lock(utilAreaStart, 32, true);
        utilArea.asIntBuffer().put(data.length());

        FileLock dataAreaLock = channel.lock(dataAreaStart, data.length(), true);
        dataArea.asCharBuffer().put(data);
        dataAreaLock.release();
        utilAreaLock.release();
    }

    protected void clearUtilArea(FileLock lock, MappedByteBuffer utilArea) throws IOException {
        utilArea.asIntBuffer().put(CLEAR_UTIL);
        lock.release();
    }

    protected void clearUtilArea(FileChannel channel, int startIndex, MappedByteBuffer utilArea) throws IOException {
        FileLock lock = channel.lock(startIndex, 32, true);
        clearUtilArea(lock, utilArea);
    }

    protected void execute(Consumer<FileChannel> method) {
        try (RandomAccessFile sharedMemory = new RandomAccessFile(SHARED_MEMORY_PATH, "rw");
             FileChannel channel = sharedMemory.getChannel()) {

            initBuffers(channel);

            method.accept(channel);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void initBuffers(FileChannel channel) throws IOException {
        this.producerUtil = channel.map(READ_WRITE, PRODUCER_OFFSET, 32);
        this.producerData = channel.map(READ_WRITE, DATA_AREA_START, SIZE / 2);
        this.consumerUtil = channel.map(READ_WRITE, CONSUMER_OFFSET, 32);
        this.consumerData = channel.map(READ_WRITE, DATA_AREA_START + SIZE / 2, SIZE / 2);
    }

}