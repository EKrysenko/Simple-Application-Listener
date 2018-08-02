package server;

import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.channels.FileLock;

import static constants.Constants.SHARED_MEMORY_PATH;
import static constants.Constants.SIZE;
import static java.nio.channels.FileChannel.MapMode.READ_WRITE;

public class EchoIPCServer implements Server {

    private static final int CLEAR_UTIL = -1;
    private static final int DATA_AREA_START = 64;
    private static final int CONSUMER_OFFSET = 32;
    private static final int PRODUCER_OFFSET = 0;

    private MappedByteBuffer consumerUtil;
    private MappedByteBuffer producerUtil;
    private MappedByteBuffer producerData;
    private MappedByteBuffer consumerData;

    private EchoIPCServer() {
    }

    public static Server createIPCServer() {
        return new EchoIPCServer();
    }

    @Override
    public void process() {
        try (RandomAccessFile sharedMemory = new RandomAccessFile(SHARED_MEMORY_PATH, "rw");
             FileChannel channel = sharedMemory.getChannel()) {

            initBuffers(channel);

            clearUtilArea(channel, producerUtil);

            while (true) {
                FileLock checkProducerUtil = channel.lock(PRODUCER_OFFSET, 32, true);
                int message = producerUtil.asIntBuffer().get();
                if (message != CLEAR_UTIL) {

                    String inputData = readData(channel, message, producerData);

                    clearUtilArea(checkProducerUtil, producerUtil);

                    String outputData = inputData;

                    writeData(outputData, channel, DATA_AREA_START + inputData.length(), consumerUtil, consumerData);
                }
                checkProducerUtil.release();
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private String readData(FileChannel channel, int dataSize, MappedByteBuffer dataArea) throws IOException {
        FileLock dataAreaLock = channel.lock(EchoIPCServer.DATA_AREA_START, dataSize, true);
        char[] chars = new char[dataSize];
        dataArea.asCharBuffer().get(chars);
        String data = new String(chars);
        dataAreaLock.release();
        return data;
    }

    private void writeData(String data, FileChannel channel, int dataAreaStart, MappedByteBuffer utilArea, MappedByteBuffer dataArea) throws IOException {
        FileLock utilAreaLock = channel.lock(EchoIPCServer.CONSUMER_OFFSET, 32, true);
        utilArea.asIntBuffer().put(data.length());

        FileLock dataAreaLock = channel.lock(dataAreaStart, data.length(), true);
        dataArea.asCharBuffer().put(data);
        dataAreaLock.release();
        utilAreaLock.release();
    }

    private void clearUtilArea(FileLock lock, MappedByteBuffer utilArea) throws IOException {
        utilArea.asIntBuffer().put(CLEAR_UTIL);
        lock.release();
    }

    private void clearUtilArea(FileChannel channel, MappedByteBuffer utilArea) throws IOException {
        FileLock lock = channel.lock(EchoIPCServer.PRODUCER_OFFSET, 32, true);
        clearUtilArea(lock, utilArea);
    }

    private void initBuffers(FileChannel channel) throws IOException {
        this.producerUtil = channel.map(READ_WRITE, PRODUCER_OFFSET, 32);
        this.producerData = channel.map(READ_WRITE, DATA_AREA_START, SIZE / 2);
        this.consumerUtil = channel.map(READ_WRITE, CONSUMER_OFFSET, 32);
        this.consumerData = channel.map(READ_WRITE, DATA_AREA_START + SIZE / 2, SIZE / 2);
    }
}
