import lombok.extern.slf4j.Slf4j;
import server.EchoIPCServer;
import server.EchoTCPServer;
import server.Server;

@Slf4j
public class MainServer {

    public static void main(String[] args) {

        if (args.length < 4) {
            log.warn("Wrong input arguments.\n"
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
            log.warn("Wrong input arguments.\n"
                    + "Use TCP/IPC.\n"
                    + "By default TCP is started");
            server = EchoTCPServer.createTCPServer();
        }

        server.perform();
    }
}