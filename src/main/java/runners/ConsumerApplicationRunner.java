package runners;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

public class ConsumerApplicationRunner {

    private static final String TCP_HOST = "localhost";
    private static final int TCP_CONSUMER_PORT = 8000;
    private static final int TCP_PRODUCER_PORT = 9000;
    private final int BLOCK_SIZE = 60_000;

    public void runCobolApp() {

        executeApp();

    }

    private void executeApp() {

        String readData;

        try (ServerSocket serverSocket = new ServerSocket(TCP_CONSUMER_PORT)) {

            Socket client = serverSocket.accept();
            try (DataInputStream dataInputStream = new DataInputStream(client.getInputStream())) {
                readData = readTCP(dataInputStream);
            }

            try (Socket socket = new Socket(TCP_HOST, TCP_PRODUCER_PORT);
                 DataOutputStream dataOutputStream = new DataOutputStream(socket.getOutputStream())) {
                writeTCP(readData, dataOutputStream);
            }

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

    private String readTCP(DataInputStream dataInputStream) throws IOException {
        final int len = dataInputStream.readInt();
        final StringBuilder sb = new StringBuilder(len * BLOCK_SIZE);
        for (int i = 0; i < len; i++)
            sb.append(dataInputStream.readUTF());
        return sb.toString();
    }

}
