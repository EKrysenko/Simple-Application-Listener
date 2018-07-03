package readers;

import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.CharBuffer;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;

import static java.nio.channels.FileChannel.MapMode;

public class SharedFileReader {

    public void readFile(String path) throws IOException {

        RandomAccessFile file = new RandomAccessFile("/dev/shm/image-cache", "rw");

        FileChannel channel = file.getChannel();

        MappedByteBuffer b = channel.map(MapMode.READ_WRITE, 0, 4096);
        CharBuffer charBuf = b.asCharBuffer();

        // Prints 'Hello server'
        char c;
        while ((c = charBuf.get()) != 0) {
            System.out.print(c);
        }
        System.out.println();

        charBuf.put(0, '\0');
    }
}