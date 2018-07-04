package writers;

import java.io.RandomAccessFile;
import java.nio.CharBuffer;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.channels.FileChannel.MapMode;

public class SharedFileWriter {

    public void writeToFile(String path, char[] outputChars) throws Exception {

        RandomAccessFile sharedMemory = new RandomAccessFile(path, "rw");

        FileChannel channel = sharedMemory.getChannel();

        MappedByteBuffer buffer = channel.map(MapMode.READ_WRITE, 0, 609600);
        CharBuffer charBuffer = buffer.asCharBuffer();
        charBuffer.clear();

//        TODO: here we cann add some logic to change input data before sending

        charBuffer.put(outputChars);
        channel.close();
    }


}
