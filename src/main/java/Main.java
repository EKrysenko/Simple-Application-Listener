import servers.EchoIPCServer;
import servers.EchoTCPServer;
import servers.Server;

public class Main {

    public static void main(String[] args) {

        if (args.length < 4) {
            System.out.println("Wrong input arguments.\n"
                    + "Specify also low size, high size for a package in bytes "
                    + "and transfer time in seconds");
            return;
        }

        Server server;

        if ("IPC".equals(args[0])) {
            server = EchoIPCServer.createIPCServer();
        } else if ("TCP".equals(args[0])) {
            server = EchoTCPServer.createTCPServer();
        } else {
            System.out.println("Wrong input arguments.\n"
                    + "Use TCP/IPC.\n"
                    + "By default TCP is started");
            server = EchoTCPServer.createTCPServer();
        }

        server.process();
    }


}
