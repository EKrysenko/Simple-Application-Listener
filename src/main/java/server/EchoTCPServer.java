package server;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.net.ServerSocket;
import java.net.Socket;

import static common.constants.TCPConstants.TCP_SERVER_PORT;

public class EchoTCPServer implements Server {

    private EchoTCPServer() {
    }

    public static Server createTCPServer() {
        return new EchoTCPServer();
    }

    @Override
    public void perform() {
        String inputData;
        String outputData;
        try (ServerSocket serverSocket = new ServerSocket(TCP_SERVER_PORT)) {
            while (true) {
                Socket client = serverSocket.accept();
                try (DataInputStream dataInputStream = new DataInputStream(client.getInputStream());
                     DataOutputStream dataOutputStream = new DataOutputStream(client.getOutputStream())) {

                    do {
                        inputData = dataInputStream.readUTF();

                        outputData = inputData; //Here we can add cobol processing
                        dataOutputStream.writeUTF(outputData);
                    } while (!inputData.equals("-1"));
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
