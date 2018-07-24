package producers;

import dataCreater.DataCreator;
import protocols.SHMTransferProtocol;

import java.io.IOException;
import java.nio.channels.FileChannel;
import java.nio.channels.FileLock;
import java.util.List;

import static constants.Constants.ARRAY_SIZE_IN_PACKAGES;

public class SHMProducer extends SHMTransferProtocol implements Producer {

    private int lowSizePackage;
    private int highSizePackage;
    private int transferTime;

    public SHMProducer(int lowSizePackage, int highSizePackage, int transferTime) {
        this.lowSizePackage = lowSizePackage;
        this.highSizePackage = highSizePackage;
        this.transferTime = transferTime;
    }

    @Override
    public void run() {
        execute(this::produce);
    }

    private void produce(FileChannel channel) {
        try {
            List<String> sendData = DataCreator.createRandomSizePackage(ARRAY_SIZE_IN_PACKAGES, lowSizePackage, highSizePackage);

            long start = System.nanoTime();
            long countOfBytes = 0;
            long countOfPackage = 0;


            label:
            while (true) {

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

        } catch (IOException e) {
            e.printStackTrace();
        }

    }
}
