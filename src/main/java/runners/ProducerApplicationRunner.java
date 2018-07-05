package runners;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.CharBuffer;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.channels.FileChannel.MapMode;

public class ProducerApplicationRunner {

    private static final int SIZE = 74883500;
    private static final String SHARED_MEMORY_PATH = "/dev/shm/image-cache";
    private static final String RECEIVED_FILE = "./received.txt";
    private static final String SEND_FILE = "./send.txt";
    private static final String NAMED_PIPE = "/home/uliana/Documents/lgi/docker/FILE.in";
    private static final String TCP_HOST = "localhost";
    private static final int TCP_CONSUMER_PORT = 8000;
    private static final int TCP_PRODUCER_PORT = 9000;

    public void runCobolApp() {

        executeApp();

    }

    private void executeApp() {

        String sendData = getOutputString(SEND_FILE);
        String readData;

        try (Socket socket = new Socket(TCP_HOST, TCP_CONSUMER_PORT);
             DataOutputStream dataOutputStream = new DataOutputStream(socket.getOutputStream());
             ServerSocket serverSocket = new ServerSocket(TCP_PRODUCER_PORT)) {

            long start = System.nanoTime();

            dataOutputStream.writeUTF(sendData);
            dataOutputStream.flush();

            Socket client = serverSocket.accept();
            try (DataInputStream dataInputStream = new DataInputStream(client.getInputStream())) {
                readData = dataInputStream.readUTF();
            }

            long finish = System.nanoTime();

            System.out.println("elapsed time is " + (finish - start) / 1e6 + " ms");
            writeReceivedToTextFile(readData.toCharArray());
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    private void writeToSHM(FileChannel channel, char[] outputChars) throws Exception {

        MappedByteBuffer buffer = channel.map(MapMode.READ_WRITE, 0, SIZE);
        CharBuffer charBuffer = buffer.asCharBuffer();
        charBuffer.clear();

        charBuffer.put(outputChars);
    }

    private char[] readSHM(FileChannel channel) throws IOException {


        MappedByteBuffer buffer = channel.map(MapMode.READ_WRITE, 0, SIZE);
        CharBuffer charBuf = buffer.asCharBuffer();
        char[] received = new char[SIZE/2];

        charBuf.get(received);

        return received;
    }

    private void writeReceivedToTextFile(char[] received) {

        try (FileWriter writer = new FileWriter(RECEIVED_FILE)) {

            writer.write(received);
            writer.flush();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private String getOutputString(String path) {

        String line;
        StringBuilder builder = new StringBuilder();
        try (BufferedReader br = new BufferedReader(new FileReader(path))) {
            while ((line = br.readLine()) != null) {
                builder.append(line).append("\n");
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        return builder.toString();
    }

}
