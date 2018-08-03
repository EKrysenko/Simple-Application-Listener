package server;

import common.IPCbase;

import java.io.RandomAccessFile;
import java.nio.channels.FileChannel;
import java.nio.channels.FileLock;

import static common.constants.IPCConstants.*;

public class EchoIPCServer extends IPCbase implements Server {

    private EchoIPCServer() {
    }

    public static Server createIPCServer() {
        return new EchoIPCServer();
    }

    @Override
    public void perform() {
        try (RandomAccessFile sharedMemory = new RandomAccessFile(SHARED_MEMORY_PATH, "rw");
             FileChannel channel = sharedMemory.getChannel()) {
            String inputData;
            String outputData;

            initBuffers(channel);
            prepareUtilArea(channel, clientUtil, CLIENT_OFFSET);

            while (true) {
                FileLock lockClientUtil = channel.lock(CLIENT_OFFSET, 32, true);
                try {
                    int message = clientUtil.asIntBuffer().get();

                    if (message != CLEAR_UTIL) {
                        inputData = readData(channel, message, DATA_AREA_START, clientData);

                        clientUtil.asIntBuffer().put(CLEAR_UTIL);
                        lockClientUtil.release();

                        outputData = inputData; //Here we can add cobol processing
                        writeData(outputData, channel, serverUtil, serverData, SERVER_OFFSET, DATA_AREA_START + inputData.length());
                    }
                } finally {
                    lockClientUtil.release();
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }


}
