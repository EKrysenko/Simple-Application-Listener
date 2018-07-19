package protocols;

import dataCreater.DataCreator;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.List;

import static constants.Constants.*;

public class TCPtransferProtocol implements TransferProtocol {

    @Override
    public void executeProducer() {

        List<String> sendData = DataCreator.createRandomSizePackage(1000, LOW_BOUNDER_IN_BYTES, UP_BOUNDER_IN_BYTES);
        try (Socket socket = new Socket(TCP_HOST, TCP_CONSUMER_PORT);
             DataOutputStream dataOutputStream = new DataOutputStream(socket.getOutputStream());
             DataInputStream dataInputStream = new DataInputStream(socket.getInputStream())) {

            long start = System.nanoTime();
            long countOfBytes = 0;
            long countOfPackage = 0;

            label:
            while (true) {

                for (String onePackage : sendData) {
                    dataOutputStream.writeUTF(onePackage);
                    String respData = dataInputStream.readUTF();
                    countOfPackage++;
                    countOfBytes += respData.length();

                    if (System.nanoTime() - start > (long) (TRANSFER_TIME_IN_SECONDS * 1e09)) {
                        break label;
                    }
                }

            }
            dataOutputStream.writeUTF("-1");

            long finish = System.nanoTime();

            System.out.println("number of packages: " + countOfPackage);
            System.out.println("Total transfer data size in Mb: " + (countOfBytes / 1024 / 1024));
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
                     DataOutputStream dataOutputStream = new DataOutputStream(client.getOutputStream())) {

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

}