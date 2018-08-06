import client.Client;
import client.IPCClient;
import client.TCPClient;

import static java.lang.Integer.parseInt;

public class MainClient {

    public static void main(String[] args) {

        if (args.length < 4) {
            System.out.println("Wrong input arguments.\n"
                    + "Specify also low size, high size for a package in bytes "
                    + "and transfer time in seconds");
            return;
        }

        Client client;

        int lowSizePackage = parseInt(args[1]);
        int highSizePackage = parseInt(args[2]);
        int transferTime = parseInt(args[3]);

        if ("IPC".equals(args[0])) {
            client = IPCClient.createIPCClient(lowSizePackage,
                    highSizePackage,
                    transferTime);
        } else if ("TCP".equals(args[0])) {
            client = TCPClient.createTCPClient(lowSizePackage,
                    highSizePackage,
                    transferTime);
        } else {
            System.out.println("Wrong input arguments.\n"
                    + "Use TCP/SHM.\n"
                    + "By default TCP is started");
            client = TCPClient.createTCPClient(lowSizePackage,
                    highSizePackage,
                    transferTime);
        }

        client.perform();
    }


}