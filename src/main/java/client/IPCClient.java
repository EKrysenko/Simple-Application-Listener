package client;

import client.dataCreater.DataCreator;
import common.IPCbase;
import lombok.extern.slf4j.Slf4j;

import java.io.RandomAccessFile;
import java.nio.channels.FileChannel;
import java.nio.channels.FileLock;
import java.util.List;

import static common.constants.IPCConstants.*;


@Slf4j
public class IPCClient extends IPCbase implements Client {
    private int lowSizePackage;
    private int highSizePackage;
    private int transferTime;

    private IPCClient(int lowSizePackage, int highSizePackage, int transferTime) {
        this.lowSizePackage = lowSizePackage;
        this.highSizePackage = highSizePackage;
        this.transferTime = transferTime;
    }

    public static Client createIPCClient(int lowSizePackage, int highSizePackage, int transferTime) {
        return new IPCClient(lowSizePackage, highSizePackage, transferTime);
    }

    @Override
    public void perform() {
        List<String> sendData = DataCreator.createRandomSizePackage(ARRAY_SIZE_IN_PACKAGES, lowSizePackage, highSizePackage);

        try (RandomAccessFile sharedMemory = new RandomAccessFile(SHARED_MEMORY_PATH, "rw");
             FileChannel channel = sharedMemory.getChannel()) {

            long start = System.nanoTime();
            long countOfBytes = 0;
            long countOfPackage = 0;

            initBuffers(channel);

            label:
            while (true) {

                for (String data : sendData) {
                    prepareUtilArea(channel, serverUtil, SERVER_OFFSET);

                    writeData(data, channel, clientUtil, clientData, CLIENT_OFFSET, DATA_AREA_START);

                    int dataSize;
                    while (true) {
                        FileLock lockServerUtil = channel.lock(SERVER_OFFSET, 32, true);
                        try {
                            if ((dataSize = serverUtil.asIntBuffer().get()) != CLEAR_UTIL) {
                                String readData = readData(channel, dataSize, DATA_AREA_START + data.length(), serverData);

                                serverUtil.asIntBuffer().put(CLEAR_UTIL);
                                lockServerUtil.release();

                                countOfPackage++;
                                countOfBytes += readData.length();
                                break;
                            }
                        } finally {
                            lockServerUtil.release();
                        }
                    }
                    if (System.nanoTime() - start > (long) (transferTime * 1e09)) {
                        break label;
                    }
                }
            }
            long finish = System.nanoTime();
            log.debug("" + countOfPackage);
            log.debug("" + countOfBytes / 1024 / 1024);
            log.debug("" + (finish - start) / 1e6 + "\n\n");
        } catch (Exception e) {
            log.error(String.valueOf(e));
        }
    }
}
