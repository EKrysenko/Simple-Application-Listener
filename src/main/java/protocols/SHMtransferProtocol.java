package protocols;

import dataCreater.DataCreator;

import java.io.RandomAccessFile;
import java.nio.channels.FileChannel;

import static constants.Constants.SHARED_MEMORY_PATH;

public class SHMtransferProtocol implements TransferProtocol {

    private final int CLEAR_UTIL = -1;
    private final int DATA_AREA_START = 64;
    private final int CONSUMER_OFFSET = 32;
    private final int PRODUCER_OFFSET = 0;

    @Override
    public void executeProducer() {

        String data = DataCreator.createFixedSizePackage(100);

        try (RandomAccessFile sharedMemory = new RandomAccessFile(SHARED_MEMORY_PATH, "rw");
             FileChannel channel = sharedMemory.getChannel()) {

            long start = System.nanoTime();

            StringBuilder sb = new StringBuilder(data);


            long finish = System.nanoTime();
            System.out.println((finish - start) / 1e6);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void executeConsumer() {

        try (RandomAccessFile sharedMemory = new RandomAccessFile(SHARED_MEMORY_PATH, "rw");
             FileChannel channel = sharedMemory.getChannel()) {



            while (true) {


            }

        } catch (Exception e) {
            e.printStackTrace();
        }

    }

}