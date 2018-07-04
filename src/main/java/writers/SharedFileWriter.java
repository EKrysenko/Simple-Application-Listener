package writers;

import java.io.File;
import java.io.RandomAccessFile;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.channels.FileChannel.MapMode;

public class SharedFileWriter {

    public void writeToFile(String path) throws Exception {

        RandomAccessFile sharedMemory = new RandomAccessFile(path, "rw");

        RandomAccessFile file = new RandomAccessFile(new File("./send.txt"), "r");
        byte[] bytes = new byte[(int) file.length()];
        file.readFully(bytes);
        FileChannel channel = sharedMemory.getChannel();

        MappedByteBuffer buffer = channel.map(MapMode.READ_WRITE, 0, 6096);
        buffer.clear();
        buffer.put(bytes);

    }

}
