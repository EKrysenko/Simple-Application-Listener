package writers;

import java.io.RandomAccessFile;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.channels.FileChannel.MapMode;

public class SharedFileWriter {

    public void writeToFile(String path, byte[] bytes) throws Exception {

        RandomAccessFile sharedMemory = new RandomAccessFile(path, "rw");

        FileChannel channel = sharedMemory.getChannel();

        MappedByteBuffer buffer = channel.map(MapMode.READ_WRITE, 0, 6096);
        buffer.clear();
        buffer.put(bytes);

    }

}
