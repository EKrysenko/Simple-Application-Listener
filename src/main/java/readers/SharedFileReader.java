package readers;

import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;

import static java.nio.channels.FileChannel.MapMode;

public class SharedFileReader {

    public byte[] readFile(String path) throws IOException {

        FileChannel channel = new RandomAccessFile(path, "rw").getChannel();

        MappedByteBuffer buffer = channel.map(MapMode.READ_WRITE, 0, 4096);

        byte[] bytes = new byte[4096];
        buffer.get(bytes);
        return bytes;

    }
}