package protocols;

import interfaces.TransferProtocol;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;

import static constants.Constants.*;

public class TCPtransferProtocol implements TransferProtocol {

    @Override
    public void executeProducer() {
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
            writeToFile(readData.toCharArray());
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    @Override
    public void executeConsumer() {
        String readData;

        try (ServerSocket serverSocket = new ServerSocket(TCP_CONSUMER_PORT)) {

            Socket client = serverSocket.accept();
            readData = readTCP(client);

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