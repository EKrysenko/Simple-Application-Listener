package server;

import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.channels.FileLock;

import static constants.IPCConstants.*;
import static java.nio.channels.FileChannel.MapMode.READ_WRITE;

public class EchoIPCServer implements Server {

    private MappedByteBuffer serverUtil;
    private MappedByteBuffer clientUtil;
    private MappedByteBuffer clientData;
    private MappedByteBuffer serverData;

    private EchoIPCServer() {
    }

    public static Server createIPCServer() {
        return new EchoIPCServer();
    }

    @Override
    public void process() {
        try (RandomAccessFile sharedMemory = new RandomAccessFile(SHARED_MEMORY_PATH, "rw");
             FileChannel channel = sharedMemory.getChannel()) {

            initBuffers(channel);

            FileLock lock = channel.lock(CLIENT_OFFSET, 32, true);
            clientUtil.asIntBuffer().put(CLEAR_UTIL);
            lock.release();

            while (true) {
                FileLock checkProducerUtil = channel.lock(CLIENT_OFFSET, 32, true);
                try {
                    int message = clientUtil.asIntBuffer().get();

                    if (message != CLEAR_UTIL) {

                        String inputData = readData(channel, message, clientData);

                        clientUtil.asIntBuffer().put(CLEAR_UTIL);
                        checkProducerUtil.release();

                        String outputData = inputData;

                        writeData(outputData, channel, DATA_AREA_START + inputData.length(), serverUtil, serverData);
                    }
                } finally {
                    checkProducerUtil.release();
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void initBuffers(FileChannel channel) throws IOException {
        this.clientUtil = channel.map(READ_WRITE, CLIENT_OFFSET, 32);
        this.clientData = channel.map(READ_WRITE, DATA_AREA_START, SIZE / 2);
        this.serverUtil = channel.map(READ_WRITE, SERVER_OFFSET, 32);
        this.serverData = channel.map(READ_WRITE, DATA_AREA_START + SIZE / 2, SIZE / 2);
    }

    private String readData(FileChannel channel, int dataSize, MappedByteBuffer dataArea) throws IOException {
        FileLock dataAreaLock = channel.lock(DATA_AREA_START, dataSize, true);
        String data;
        try {
            char[] chars = new char[dataSize];
            dataArea.asCharBuffer().get(chars);
            data = new String(chars);
        } finally {
            dataAreaLock.release();
        }

        return data;
    }

    private void writeData(String data, FileChannel channel, int dataAreaStart, MappedByteBuffer utilArea, MappedByteBuffer dataArea) throws IOException {
        FileLock utilAreaLock = channel.lock(SERVER_OFFSET, 32, true);
        FileLock dataAreaLock = channel.lock(dataAreaStart, data.length(), true);
        try {
            utilArea.asIntBuffer().put(data.length());
            dataArea.asCharBuffer().put(data);
        } finally {
            dataAreaLock.release();
            utilAreaLock.release();
        }
    }

}
