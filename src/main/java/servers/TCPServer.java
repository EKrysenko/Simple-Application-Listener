package servers;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.net.ServerSocket;
import java.net.Socket;

import static constants.Constants.TCP_CONSUMER_PORT;

public class TCPServer implements Server {

    private TCPServer() {
    }

    public static Server getTCPServer() {
        return new TCPServer();
    }

    @Override
    public void run() {
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
