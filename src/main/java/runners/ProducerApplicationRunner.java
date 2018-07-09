package runners;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;

public class ProducerApplicationRunner {

    private static final String RECEIVED_FILE = "./received.txt";
    private static final String SEND_FILE = "./send_100.txt";
    private static final String TCP_HOST = "localhost";
    private static final int TCP_CONSUMER_PORT = 8000;
    private static final int TCP_PRODUCER_PORT = 9000;
    static final int BLOCK_SIZE = 60_000;

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

            writeTCP(sendData, dataOutputStream);

            Socket client = serverSocket.accept();

            readData = readTCP(client);

            long finish = System.nanoTime();

            System.out.println("elapsed time is " + (finish - start) / 1e6 + " ms");
            writeReceivedToTextFile(readData.toCharArray());
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    private void writeTCP(String sendData, DataOutputStream dataOutputStream) throws IOException {
        int stringLength = sendData.length();
        int number = stringLength / BLOCK_SIZE;
        dataOutputStream.writeInt(number);
        String[] stringArr = new String[number];

        for (int i = 0; i < number; i++) {
            final int beginIndex = i * BLOCK_SIZE;
            final int lastInd = beginIndex + BLOCK_SIZE;
            if (lastInd > stringLength)
                stringArr[i] = sendData.substring(beginIndex, stringLength);
            else
                stringArr[i] = sendData.substring(beginIndex, lastInd);
            dataOutputStream.writeUTF(stringArr[i]);
        }
        dataOutputStream.flush();
    }

    private String readTCP(Socket client) throws IOException {
        final StringBuilder sb;
        try (DataInputStream dataInputStream = new DataInputStream(client.getInputStream())) {
            final int len = dataInputStream.readInt();
            sb = new StringBuilder(len * BLOCK_SIZE);
            for (int i = 0; i < len; i++)
                sb.append(dataInputStream.readUTF());
        }
        return sb.toString();
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
