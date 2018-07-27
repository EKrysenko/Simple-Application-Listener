package client;

import dataCreater.DataCreator;

import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.channels.FileLock;
import java.util.List;

import static constants.Constants.*;
import static java.nio.channels.FileChannel.MapMode.READ_WRITE;

public class IPCClient implements Client {

    private static final int CLEAR_UTIL = -1;
    private static final int DATA_AREA_START = 64;
    private static final int CONSUMER_OFFSET = 32;
    private static final int PRODUCER_OFFSET = 0;

    private MappedByteBuffer consumerUtil;
    private MappedByteBuffer producerUtil;
    private MappedByteBuffer producerData;
    private MappedByteBuffer consumerData;

    private int lowSizePackage;
    private int highSizePackage;
    private int transferTime;

    private IPCClient(int lowSizePackage, int highSizePackage, int transferTime) {
        this.lowSizePackage = lowSizePackage;
        this.highSizePackage = highSizePackage;
        this.transferTime = transferTime;
    }


    public static Client getIPCClient(int lowSizePackage, int highSizePackage, int transferTime) {
        return new IPCClient(lowSizePackage, highSizePackage, transferTime);
    }

    @Override
    public void run() {
        try (RandomAccessFile sharedMemory = new RandomAccessFile(SHARED_MEMORY_PATH, "rw");
             FileChannel channel = sharedMemory.getChannel()) {

            List<String> sendData = DataCreator.createRandomSizePackage(ARRAY_SIZE_IN_PACKAGES, lowSizePackage, highSizePackage);

            long start = System.nanoTime();
            long countOfBytes = 0;
            long countOfPackage = 0;

            initBuffers(channel);

            label:
            while (true) {

                for (String data : sendData) {

                    clearUtilArea(channel, consumerUtil);

                    writeData(data, channel, producerUtil, producerData);

                    int dataSize;
                    FileLock checkConsumerUtil;
                    while (true) {
                        checkConsumerUtil = channel.lock(CONSUMER_OFFSET, 32, true);
                        if ((dataSize = consumerUtil.asIntBuffer().get()) != CLEAR_UTIL) {
                            break;
                        }
                        checkConsumerUtil.release();
                    }

                    String readData = readData(channel, dataSize, DATA_AREA_START + data.length(), consumerData);

                    clearUtilArea(checkConsumerUtil, consumerUtil);

                    countOfPackage++;
                    countOfBytes += readData.length();

                    if (System.nanoTime() - start > (long) (transferTime * 1e09)) {
                        break label;
                    }
                }
            }

            long finish = System.nanoTime();

            System.out.println("Number of packages\nNumber of Mb\nTime\n");
            System.out.println(countOfPackage);
            System.out.println(countOfBytes / 1024 / 1024);
            System.out.println((finish - start) / 1e6 + "\n\n");

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private String readData(FileChannel channel, int dataSize, int startIndex, MappedByteBuffer dataArea) throws IOException {
        FileLock dataAreaLock = channel.lock(startIndex, dataSize, true);
        char[] chars = new char[dataSize];
        dataArea.asCharBuffer().get(chars);
        String data = new String(chars);
        dataAreaLock.release();
        return data;
    }

    private void writeData(String data, FileChannel channel, MappedByteBuffer utilArea, MappedByteBuffer dataArea) throws IOException {
        FileLock utilAreaLock = channel.lock(IPCClient.PRODUCER_OFFSET, 32, true);
        utilArea.asIntBuffer().put(data.length());

        FileLock dataAreaLock = channel.lock(IPCClient.DATA_AREA_START, data.length(), true);
        dataArea.asCharBuffer().put(data);
        dataAreaLock.release();
        utilAreaLock.release();
    }

    private void clearUtilArea(FileLock lock, MappedByteBuffer utilArea) throws IOException {
        utilArea.asIntBuffer().put(CLEAR_UTIL);
        lock.release();
    }

    private void clearUtilArea(FileChannel channel, MappedByteBuffer utilArea) throws IOException {
        FileLock lock = channel.lock(IPCClient.CONSUMER_OFFSET, 32, true);
        clearUtilArea(lock, utilArea);
    }

    private void initBuffers(FileChannel channel) throws IOException {
        this.producerUtil = channel.map(READ_WRITE, PRODUCER_OFFSET, 32);
        this.producerData = channel.map(READ_WRITE, DATA_AREA_START, SIZE / 2);
        this.consumerUtil = channel.map(READ_WRITE, CONSUMER_OFFSET, 32);
        this.consumerData = channel.map(READ_WRITE, DATA_AREA_START + SIZE / 2, SIZE / 2);
    }
}
