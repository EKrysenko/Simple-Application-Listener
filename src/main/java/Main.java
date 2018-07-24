import factories.SHMFactory;
import factories.TransferProtocolFactory;
import factories.TCPFactory;

import static java.lang.Integer.*;

public class Main {

    public static void main(String[] args) {

        if (args.length < 5 && "producer".equals(args[1])) {
            System.out.println("Wrong input arguments.\n"
                    + "Specify also low size, high size for a package in bytes "
                    + "and transfer time in seconds");
        }

        TransferProtocolFactory factory;

        switch (args[0]) {
            case "TCP":
                factory = new TCPFactory();
                break;

            case "SHM":
                factory = new SHMFactory();
                break;
            default:
                System.out.println("Wrong input arguments.\n"
                        + "Use TCP/HDD/SHM for protocol and producer/consumer for executor");
                return;
        }

        switch (args[1]) {
            case "consumer":
                factory.createConsumer().run();
                return;
            case "producer":
                factory.createProducer(
                        parseInt(args[2]),
                        parseInt(args[3]),
                        parseInt(args[4]))
                        .run();
                return;
            default:
                System.out.println("Wrong input arguments.\n"
                        + "Use TCP/HDD/SHM for protocol and producer/consumer for executor");
        }

    }

}
