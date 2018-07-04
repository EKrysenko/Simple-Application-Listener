package readers;

import java.io.*;
import java.nio.CharBuffer;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;

import static java.nio.channels.FileChannel.MapMode;

public class SharedFileReader {

    public char[] readFile(String path) throws IOException {

        RandomAccessFile file = new RandomAccessFile(path, "rw");

        FileChannel channel = file.getChannel();

        MappedByteBuffer buffer = channel.map(MapMode.READ_WRITE, 0, 609600);
        CharBuffer charBuf = buffer.asCharBuffer();
        char[] received = new char[4096];

        charBuf.get(received);
        channel.close();

        return received;
    }
}