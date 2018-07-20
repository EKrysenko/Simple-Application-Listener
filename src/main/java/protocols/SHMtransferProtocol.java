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
    public void executeProducer() {

        List<String> sendData = DataCreator.createRandomSizePackage(1000, lowSizePackage, highSizePackage);

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

                    clearUtilArea(channel, CONSUMER_OFFSET);

                    writeData(data, channel, PRODUCER_OFFSET, DATA_AREA_START, consumerUtilBuffer, producerDataBuffer);

                    int dataSize;
                    FileLock checkConsumerUtil;
                    while (true) {
                        checkConsumerUtil = channel.lock(CONSUMER_OFFSET, 32, true);
                        if ((dataSize = consumerUtilBuffer.asIntBuffer().get()) != CLEAR_UTIL) {
                            break;
                        }
                        checkConsumerUtil.release();
                    }

                    String readData = readData(channel, dataSize, DATA_AREA_START + data.length());

                    clearUtilArea(channel, checkConsumerUtil, CONSUMER_OFFSET);

                    countOfPackage++;
                    countOfBytes += readData.length();

                    if (System.nanoTime() - start > (long) (transferTime * 1e09)) {
                        break label;
                    }
                }
            }

            long finish = System.nanoTime();

            System.out.println("number of packages: " + countOfPackage);
            System.out.println("Total transfer data size in Mb: " + (countOfBytes / 1024 / 1024));
            System.out.println((finish - start) / 1e6);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void executeConsumer() {

        try (RandomAccessFile sharedMemory = new RandomAccessFile(SHARED_MEMORY_PATH, "rw");
             FileChannel channel = sharedMemory.getChannel()) {

            producerUtilBuffer = channel.map(READ_WRITE, PRODUCER_OFFSET, 32);
            producerDataBuffer = channel.map(READ_WRITE, DATA_AREA_START, SIZE / 2);
            consumerDataBuffer = channel.map(READ_WRITE, DATA_AREA_START + SIZE / 2, SIZE / 2);

            clearUtilArea(channel, PRODUCER_OFFSET);

            while (true) {
                FileLock checkProducerUtil = channel.lock(PRODUCER_OFFSET, 32, true);
                int message = producerUtilBuffer.asIntBuffer().get();
                if (message != CLEAR_UTIL) {

                    String inputData = readData(channel, message, DATA_AREA_START);

                    clearUtilArea(channel, checkProducerUtil, PRODUCER_OFFSET);

                    String outputData = inputData;

                    writeData(outputData, channel, CONSUMER_OFFSET, DATA_AREA_START + inputData.length(), consumerUtilBuffer, consumerDataBuffer);
                }
                checkProducerUtil.release();
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    private String readData(FileChannel channel, int dataSize, int startIndex) throws IOException {
        FileLock dataAreaLock = channel.lock(startIndex, dataSize, true);
        char[] chars = new char[dataSize];
        channel.map(READ_WRITE, startIndex, SIZE).asCharBuffer().get(chars);
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

    private void clearUtilArea(FileChannel channel, FileLock lock, int producer_offset) throws IOException {
        channel.map(READ_WRITE, producer_offset, SIZE).asIntBuffer().put(CLEAR_UTIL);
        lock.release();
    }

    private void clearUtilArea(FileChannel channel, int startIndex) throws IOException {
        FileLock lock = channel.lock(startIndex, 32, true);
        clearUtilArea(channel, lock, startIndex);
    }

}