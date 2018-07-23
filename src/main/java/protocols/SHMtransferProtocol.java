package protocols;

import dataCreater.DataCreator;

import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.channels.FileLock;
import java.util.List;

import static constants.Constants.*;
import static java.nio.channels.FileChannel.MapMode.READ_WRITE;

public class SHMtransferProtocol implements TransferProtocol {

    private final int CLEAR_UTIL = -1;
    private final int DATA_AREA_START = 64;
    private final int CONSUMER_OFFSET = 32;
    private final int PRODUCER_OFFSET = 0;
    private MappedByteBuffer consumerUtilBuffer;
    private MappedByteBuffer producerUtilBuffer;
    private MappedByteBuffer producerDataBuffer;
    private MappedByteBuffer consumerDataBuffer;

    @Override
    public void executeProducer(int lowSizePackage, int highSizePackage, int transferTime) {

        List<String> sendData = DataCreator.createRandomSizePackage(ARRAY_SIZE_IN_PACKAGES, lowSizePackage, highSizePackage);

        try (RandomAccessFile sharedMemory = new RandomAccessFile(SHARED_MEMORY_PATH, "rw");
             FileChannel channel = sharedMemory.getChannel()) {

            long start = System.nanoTime();
            long countOfBytes = 0;
            long countOfPackage = 0;

            producerUtilBuffer = channel.map(READ_WRITE, PRODUCER_OFFSET, 32);
            producerDataBuffer = channel.map(READ_WRITE, DATA_AREA_START, SIZE / 2);
            consumerUtilBuffer = channel.map(READ_WRITE, CONSUMER_OFFSET, 32);
            consumerDataBuffer = channel.map(READ_WRITE, DATA_AREA_START + SIZE / 2, SIZE / 2);

            label:
            while(true) {

                for (String data : sendData) {

                    clearUtilArea(channel, CONSUMER_OFFSET, consumerUtilBuffer);

                    writeData(data, channel, PRODUCER_OFFSET, DATA_AREA_START, producerUtilBuffer, producerDataBuffer);

                    int dataSize;
                    FileLock checkConsumerUtil;
                    while (true) {
                        checkConsumerUtil = channel.lock(CONSUMER_OFFSET, 32, true);
                        if ((dataSize = consumerUtilBuffer.asIntBuffer().get()) != CLEAR_UTIL) {
                            break;
                        }
                        checkConsumerUtil.release();
                    }

                    String readData = readData(channel, dataSize, DATA_AREA_START + data.length(), consumerDataBuffer);

                    clearUtilArea(checkConsumerUtil, consumerUtilBuffer);

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

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void executeConsumer() {

        try (RandomAccessFile sharedMemory = new RandomAccessFile(SHARED_MEMORY_PATH, "rw");
             FileChannel channel = sharedMemory.getChannel()) {

            producerUtilBuffer = channel.map(READ_WRITE, PRODUCER_OFFSET, 32);
            consumerUtilBuffer = channel.map(READ_WRITE, CONSUMER_OFFSET, 32);
            producerDataBuffer = channel.map(READ_WRITE, DATA_AREA_START, SIZE / 2);
            consumerDataBuffer = channel.map(READ_WRITE, DATA_AREA_START + SIZE / 2, SIZE / 2);

            clearUtilArea(channel, PRODUCER_OFFSET, producerUtilBuffer);

            while (true) {
                FileLock checkProducerUtil = channel.lock(PRODUCER_OFFSET, 32, true);
                int message = producerUtilBuffer.asIntBuffer().get();
                if (message != CLEAR_UTIL) {

                    String inputData = readData(channel, message, DATA_AREA_START, producerDataBuffer);

                    clearUtilArea(checkProducerUtil, producerUtilBuffer);

                    String outputData = inputData;

                    writeData(outputData, channel, CONSUMER_OFFSET, DATA_AREA_START + inputData.length(), consumerUtilBuffer, consumerDataBuffer);
                }
                checkProducerUtil.release();
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    private String readData(FileChannel channel, int dataSize, int startIndex, MappedByteBuffer dataAreaBuffer) throws IOException {
        FileLock dataAreaLock = channel.lock(startIndex, dataSize, true);
        char[] chars = new char[dataSize];
        dataAreaBuffer.asCharBuffer().get(chars);
        String data = new String(chars);
        dataAreaLock.release();
        return data;
    }

    private void writeData(String data, FileChannel channel, int utilAreaStart, int dataAreaStart, MappedByteBuffer utilAreaBuffer, MappedByteBuffer dataAreaBuffer) throws IOException {
        FileLock utilAreaLock = channel.lock(utilAreaStart, 32, true);
        utilAreaBuffer.asIntBuffer().put(data.length());

        FileLock dataAreaLock = channel.lock(dataAreaStart, data.length(), true);
        dataAreaBuffer.asCharBuffer().put(data);
        dataAreaLock.release();
        utilAreaLock.release();
    }

    private void clearUtilArea(FileLock lock, MappedByteBuffer utilAreaBuffer) throws IOException {
        utilAreaBuffer.asIntBuffer().put(CLEAR_UTIL);
        lock.release();
    }

    private void clearUtilArea(FileChannel channel, int startIndex, MappedByteBuffer utilAreaBuffer) throws IOException {
        FileLock lock = channel.lock(startIndex, 32, true);
        clearUtilArea(lock, utilAreaBuffer);
    }

}