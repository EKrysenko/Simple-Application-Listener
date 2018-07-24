import factories.SHMFactory;
import factories.TCPFactory;
import factories.TransferProtocolFactory;

import static java.lang.Integer.parseInt;

public class Main {

    public static void main(String[] args) {

        if (args.length < 5 && "producer".equals(args[1])) {
            System.out.println("Wrong input arguments.\n"
                    + "Specify also low size, high size for a package in bytes "
                    + "and transfer time in seconds");
            return;
        }

        TransferProtocolFactory factory = getFactory(args[0]);

        if ("producer".equals(args[1])) {
            factory.createProducer(
                    parseInt(args[2]),
                    parseInt(args[3]),
                    parseInt(args[4]))
                    .run();
        }
        if ("consumer".equals(args[1])) {
            factory.createConsumer().run();
        }
        System.out.println("Wrong input arguments.\n"
                + "Use producer/consumer for executor. \n" +
                "by default consumer is created");

    }

    private static TransferProtocolFactory getFactory(String protocol) {
        if ("SHM".equals(protocol)) {
            return new SHMFactory();
        }
        if ("TCP".equals(protocol)) {
            return new TCPFactory();
        } else {
            System.out.println("Wrong input arguments.\n"
                    + "Use TCP/SHM.\n"
                    + "By default TCP is started");
            return new TCPFactory();
        }
    }

}
