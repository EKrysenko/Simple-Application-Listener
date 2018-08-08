package client;

import client.dataCreater.DataCreator;
import lombok.extern.slf4j.Slf4j;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.net.Socket;
import java.util.List;

import static common.constants.TCPConstants.*;


@Slf4j
public class TCPClient implements Client {

    private int lowSizePackage;
    private int highSizePackage;
    private int transferTime;

    private TCPClient(int lowSizePackage, int highSizePackage, int transferTime) {
        this.lowSizePackage = lowSizePackage;
        this.highSizePackage = highSizePackage;
        this.transferTime = transferTime;
    }

    public static Client createTCPClient(int lowSizePackage, int highSizePackage, int transferTime) {
        return new TCPClient(lowSizePackage, highSizePackage, transferTime);
    }

    @Override
    public void perform() {

        try (Socket socket = new Socket(TCP_HOST, TCP_SERVER_PORT);
             DataOutputStream dataOutputStream = new DataOutputStream(socket.getOutputStream());
             DataInputStream dataInputStream = new DataInputStream(socket.getInputStream())) {

            List<String> sendData = DataCreator.createRandomSizePackage(ARRAY_SIZE_IN_PACKAGES,
                    lowSizePackage,
                    highSizePackage);

            socket.setKeepAlive(true);

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

                    if (System.nanoTime() - start > (long) (transferTime * 1e09)) {
                        break label;
                    }
                }

            }
            dataOutputStream.writeUTF("-1");

            long finish = System.nanoTime();

            log.info("" + countOfPackage);
            log.info("" + countOfBytes / 1024 / 1024);
            log.info("" + (finish - start) / 1e6 + "\n");
        } catch (Exception e) {
            log.error(String.valueOf(e));
        }
    }
}
