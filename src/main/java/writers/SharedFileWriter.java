package writers;

import java.io.RandomAccessFile;
import java.nio.CharBuffer;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.channels.FileChannel.MapMode;

public class SharedFileWriter {

    public void writeToFile(String path, String text) throws Exception {

        RandomAccessFile file = new RandomAccessFile("/dev/shm/image-cache", "rw");

        FileChannel channel = file.getChannel();

        MappedByteBuffer buffer = channel.map(MapMode.READ_WRITE, 0, 4096);
        CharBuffer charBuf = buffer.asCharBuffer();

        String textToSend = text + "\0";
        char[] message = textToSend.toCharArray();
        charBuf.put(message);

        System.out.println("Waiting for client.");
        while (charBuf.get(0) != '\0') ;
        System.out.println("Finished waiting.");

    }

}
