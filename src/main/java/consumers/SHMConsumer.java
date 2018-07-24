package consumers;

import protocols.SHMTransferProtocol;

import java.io.IOException;
import java.nio.channels.FileChannel;
import java.nio.channels.FileLock;

public class SHMConsumer extends SHMTransferProtocol implements Consumer {


    public void run() {
        execute(this::consume);
    }


    private void consume(FileChannel channel) {
        try {
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
        } catch (IOException e) {
            e.printStackTrace();
        }

    }
}
