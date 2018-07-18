package protocols;

import dataCreater.DataCreater;

import java.io.IOException;
import java.io.RandomAccessFile;
import java.io.UnsupportedEncodingException;
import java.nio.channels.FileChannel;
import java.nio.channels.FileLock;

import static constants.Constants.SHARED_MEMORY_PATH;
import static constants.Constants.SIZE;
import static java.nio.channels.FileChannel.MapMode.READ_WRITE;

public class SHMtransferProtocol implements TransferProtocol {

    private final int CLEAR_UTIL = -1;
    private final int DATA_AREA_START = 64;
    private final int CONSUMER_OFFSET = 32;
    private final int PRODUCER_OFFSET = 0;

    @Override
    public void executeProducer() {
        String data = "";
        try {
            data = DataCreater.create(100.);
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }

        try (RandomAccessFile sharedMemory = new RandomAccessFile(SHARED_MEMORY_PATH, "rw");
             FileChannel channel = sharedMemory.getChannel()) {

            long start = System.nanoTime();

            clearUtilArea(channel, CONSUMER_OFFSET);

            writeData(data, channel, PRODUCER_OFFSET, DATA_AREA_START);

            int dataSize = 0;
            FileLock checkConsumerUtil;
            while (true) {
                checkConsumerUtil = channel.lock(CONSUMER_OFFSET, 32, false);
                if ((dataSize = channel.map(READ_WRITE, CONSUMER_OFFSET, SIZE).asIntBuffer().get()) != CLEAR_UTIL) {
                    break;
                }
                checkConsumerUtil.release();
            }
            String inputData = readData(channel, dataSize, DATA_AREA_START + data.length());

            clearUtilArea(channel, checkConsumerUtil, CONSUMER_OFFSET);

            long finish = System.nanoTime();
            System.out.println(inputData.equals(data));
            System.out.println((finish - start) / 1e6);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void executeConsumer() {

        try (RandomAccessFile sharedMemory = new RandomAccessFile(SHARED_MEMORY_PATH, "rw");
             FileChannel channel = sharedMemory.getChannel()) {

            clearUtilArea(channel, PRODUCER_OFFSET);

            while (true) {
                FileLock checkProducerUtil = channel.lock(PRODUCER_OFFSET, 32, false);
                int message = channel.map(READ_WRITE, PRODUCER_OFFSET, SIZE).asIntBuffer().get();
                if (message != CLEAR_UTIL) {

                    String inputData = readData(channel, message, DATA_AREA_START);

                    clearUtilArea(channel, checkProducerUtil, PRODUCER_OFFSET);

                    String outputData = inputData;

                    writeData(outputData, channel, CONSUMER_OFFSET, DATA_AREA_START + inputData.length());
                }
                checkProducerUtil.release();
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    private String readData(FileChannel channel, int dataSize, int startIndex) throws IOException {
        FileLock dataAreaLock = channel.lock(startIndex, dataSize, false);
        char[] chars = new char[dataSize];
        channel.map(READ_WRITE, startIndex, SIZE).asCharBuffer().get(chars);
        String data = new String(chars);
        dataAreaLock.release();
        return data;
    }

    private void writeData(String data, FileChannel channel, int utilAreaStart, int dataAreaStart) throws IOException {
        FileLock utilAreaLock = channel.lock(utilAreaStart, 32, false);
        channel.map(READ_WRITE, utilAreaStart, SIZE).asIntBuffer().put(data.length());

        FileLock dataAreaLock = channel.lock(dataAreaStart, data.length(), false);
        channel.map(READ_WRITE, dataAreaStart, SIZE).asCharBuffer().put(data);
        dataAreaLock.release();
        utilAreaLock.release();
    }

    private void clearUtilArea(FileChannel channel, FileLock lock, int producer_offset) throws IOException {
        channel.map(READ_WRITE, producer_offset, SIZE).asIntBuffer().put(CLEAR_UTIL);
        lock.release();
    }

    private void clearUtilArea(FileChannel channel, int startIndex) throws IOException {
        FileLock lock = channel.lock(startIndex, 32, false);
        clearUtilArea(channel, lock, startIndex);
    }

}