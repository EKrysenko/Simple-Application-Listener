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

    private MappedByteBuffer consumerUtil;
    private MappedByteBuffer producerUtil;
    private MappedByteBuffer producerData;
    private MappedByteBuffer consumerData;

    @Override
    public void executeProducer(int lowSizePackage, int highSizePackage, int transferTime) {

        List<String> sendData = DataCreator.createRandomSizePackage(ARRAY_SIZE_IN_PACKAGES, lowSizePackage, highSizePackage);

        try (RandomAccessFile sharedMemory = new RandomAccessFile(SHARED_MEMORY_PATH, "rw");
             FileChannel channel = sharedMemory.getChannel()) {

            long start = System.nanoTime();
            long countOfBytes = 0;
            long countOfPackage = 0;

            initBuffers(channel);

            label:
            while(true) {

                for (String data : sendData) {

                    clearUtilArea(channel, CONSUMER_OFFSET, consumerUtil);

                    writeData(data, channel, PRODUCER_OFFSET, DATA_AREA_START, producerUtil, producerData);

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

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void executeConsumer() {

        try (RandomAccessFile sharedMemory = new RandomAccessFile(SHARED_MEMORY_PATH, "rw");
             FileChannel channel = sharedMemory.getChannel()) {

            initBuffers(channel);

            clearUtilArea(channel, PRODUCER_OFFSET, producerUtil);

            while (true) {
                FileLock checkProducerUtil = channel.lock(PRODUCER_OFFSET, 32, true);
                int message = producerUtil.asIntBuffer().get();
                if (message != CLEAR_UTIL) {

                    String inputData = readData(channel, message, DATA_AREA_START, producerData);

                    clearUtilArea(checkProducerUtil, producerUtil);

                    String outputData = inputData;

                    writeData(outputData, channel, CONSUMER_OFFSET, DATA_AREA_START + inputData.length(), consumerUtil, consumerData);
                }
                checkProducerUtil.release();
            }

        } catch (Exception e) {
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

    private void writeData(String data, FileChannel channel, int utilAreaStart, int dataAreaStart, MappedByteBuffer utilArea, MappedByteBuffer dataArea) throws IOException {
        FileLock utilAreaLock = channel.lock(utilAreaStart, 32, true);
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

    private void clearUtilArea(FileChannel channel, int startIndex, MappedByteBuffer utilArea) throws IOException {
        FileLock lock = channel.lock(startIndex, 32, true);
        clearUtilArea(lock, utilArea);
    }

    private void initBuffers(FileChannel channel) throws IOException {
        this.producerUtil = channel.map(READ_WRITE, PRODUCER_OFFSET, 32);
        this.producerData = channel.map(READ_WRITE, DATA_AREA_START, SIZE / 2);
        this.consumerUtil = channel.map(READ_WRITE, CONSUMER_OFFSET, 32);
        this.consumerData = channel.map(READ_WRITE, DATA_AREA_START + SIZE / 2, SIZE / 2);
    }

}