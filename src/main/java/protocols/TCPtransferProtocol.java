package protocols;

import dataCreater.DataCreator;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.List;
import java.util.concurrent.TimeUnit;

import static constants.Constants.*;

public class TCPtransferProtocol implements TransferProtocol {

    @Override
    public void executeProducer() {

        List<String> sendData = null;
        try {
            sendData = DataCreator.createRandomSizePackage(2 * 1024, LOW_BOUNDER_IN_BYTES, UP_BOUNDER_IN_BYTES);
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }

        try (ServerSocket serverSocket = new ServerSocket(TCP_PRODUCER_PORT);
             Socket socket = new Socket(TCP_HOST, TCP_CONSUMER_PORT);
             DataOutputStream dataOutputStream = new DataOutputStream(socket.getOutputStream())) {

            TimeUnit.MILLISECONDS.sleep(10);

            Socket client = serverSocket.accept();

            long start = System.nanoTime();
            long i = 0;
            long j = 0;
            try (DataInputStream dataInputStream = new DataInputStream(client.getInputStream())) {

                while (System.nanoTime() - start < (long) (TRANSFER_TIME_IN_SECONDS * 1e09)) {
                    for (String onePackage : sendData) {
                        dataOutputStream.writeUTF(onePackage);
                        String respData = dataInputStream.readUTF();
                        j++;
                        i += respData.length();
                    }
                }
                dataOutputStream.writeUTF("-1");
            }

            long finish = System.nanoTime();

            System.out.println("number of packages: " + j);
            System.out.println("Total transfer data size in Mb: " + (i / 1024 / 1024));
            System.out.println((finish - start) / 1e6);

        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    @Override
    public void executeConsumer() {
        String readData;

        try (ServerSocket serverSocket = new ServerSocket(TCP_CONSUMER_PORT)) {

            while (true) {
                Socket client = serverSocket.accept();
                try (DataInputStream dataInputStream = new DataInputStream(client.getInputStream());
                     Socket socket = new Socket(TCP_HOST, TCP_PRODUCER_PORT);
                     DataOutputStream dataOutputStream = new DataOutputStream(socket.getOutputStream())) {

                    do {
                        readData = dataInputStream.readUTF();
                        dataOutputStream.writeUTF(readData);
                    } while (!readData.equals("-1"));
                }
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

    private void writeToFile(char[] received) {

        try (FileWriter writer = new FileWriter(RECEIVED_FILE)) {

            writer.write(received);
            writer.flush();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}