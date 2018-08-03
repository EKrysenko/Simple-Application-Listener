package common;

import java.io.IOException;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.channels.FileLock;

import static common.constants.IPCConstants.*;
import static java.nio.channels.FileChannel.MapMode.READ_WRITE;

public class IPCbase {

    protected MappedByteBuffer serverUtil;
    protected MappedByteBuffer clientUtil;
    protected MappedByteBuffer serverData;
    protected MappedByteBuffer clientData;

    protected void initBuffers(FileChannel channel) throws IOException {
        this.clientUtil = channel.map(READ_WRITE, CLIENT_OFFSET, 32);
        this.clientData = channel.map(READ_WRITE, DATA_AREA_START, SIZE / 2);
        this.serverUtil = channel.map(READ_WRITE, SERVER_OFFSET, 32);
        this.serverData = channel.map(READ_WRITE, DATA_AREA_START + SIZE / 2, SIZE / 2);
    }

    protected void prepareUtilArea(FileChannel channel, MappedByteBuffer utilArea, int utilAreaStart) throws IOException {
        FileLock lock = channel.lock(utilAreaStart, 32, true);

        try {
            utilArea.asIntBuffer().put(CLEAR_UTIL);
        } finally {
            lock.release();
        }
    }

    protected String readData(FileChannel channel, int dataSize, int startIndex, MappedByteBuffer dataArea) throws IOException {
        FileLock dataAreaLock = channel.lock(startIndex, dataSize, true);
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

    protected void writeData(String data, FileChannel channel, MappedByteBuffer utilArea, MappedByteBuffer dataArea, int utilAreaStart, int dataAreaStart) throws IOException {
        FileLock utilAreaLock = channel.lock(utilAreaStart, 32, true);
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
