package runners;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.CharBuffer;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;

import static java.nio.channels.FileChannel.MapMode;

public class ConsumerApplicationRunner {

    private static final String PATH = "/dev/shm/image-cache";
    private static final String MODE = "rw";
    private static final String NAMED_PIPE = "/home/uliana/Documents/lgi/docker/FILE.in";
    private static final int SIZE = 74883500;
    private static final String TCP_HOST = "localhost";
    private static final int TCP_CONSUMER_PORT = 8000;
    private static final int TCP_PRODUCER_PORT = 9000;

    public void runCobolApp() throws FileNotFoundException {

        executeApp();

    }

    private void executeApp() throws FileNotFoundException {

        String readData;

        try (ServerSocket serverSocket = new ServerSocket(TCP_CONSUMER_PORT)) {

//            long start = System.nanoTime();

            Socket client = serverSocket.accept();
            try (DataInputStream dataInputStream = new DataInputStream(client.getInputStream())) {
                readData = dataInputStream.readUTF();
            }

            try (Socket socket = new Socket(TCP_HOST, TCP_PRODUCER_PORT);
                 DataOutputStream dataOutputStream = new DataOutputStream(socket.getOutputStream())) {
                dataOutputStream.writeUTF(readData);
                dataOutputStream.flush();
            }

//            long finish = System.nanoTime();

//            System.out.println("elapsed time is " + (finish - start) / 1e6 + " ms");
        } catch (Exception e) {
            e.printStackTrace();
        }

    }


    private char[] readFromSHM(FileChannel channel) throws IOException {

        MappedByteBuffer buffer = channel.map(MapMode.READ_WRITE, 0, SIZE);
        CharBuffer charBuf = buffer.asCharBuffer();
        char[] received = new char[SIZE/2];

        charBuf.get(received);

        return received;
    }

    private void writeToSHM(FileChannel channel, char[] outputChars) throws Exception {


        MappedByteBuffer buffer = channel.map(MapMode.READ_WRITE, 0, SIZE);
        CharBuffer charBuffer = buffer.asCharBuffer();
        charBuffer.clear();

        charBuffer.put(outputChars);
    }
}
