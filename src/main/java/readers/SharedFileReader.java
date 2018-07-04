package readers;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;

import static java.nio.channels.FileChannel.MapMode;

public class SharedFileReader {

    public void readFile(String path) throws IOException {

        RandomAccessFile file = new RandomAccessFile(path, "rw");

        FileChannel channel = file.getChannel();

        MappedByteBuffer buffer = channel.map(MapMode.READ_WRITE, 0, 4096);
        byte[] bytes = new byte[4096];
        buffer.get(bytes);

        FileOutputStream outputStream = new FileOutputStream("./received.txt");

        outputStream.write(bytes);

    }
}