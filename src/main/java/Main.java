import protocols.SHMtransferProtocol;
import protocols.TCPtransferProtocol;
import protocols.TransferProtocol;

public class Main {

    public static void main(String[] args) {

        TransferProtocol transferProtocol;

        switch (args[0]) {
            case "TCP":
                transferProtocol = new TCPtransferProtocol();
                break;

            case "SHM":
                transferProtocol = new SHMtransferProtocol();
                break;
            default:
                System.out.println("Wrong input arguments.\n"
                        + "Use TCP/HDD/SHM for protocol and producer/consumer for executor");
                return;
        }

        if (args.length < 5) {
            System.out.println("Wrong input arguments.\n"
                    + "Specify also low size, high size for a package in bytes and transfer time in seconds");
        } else {
            transferProtocol.execute(args[1], args[2], args[3], args[4]);
        }
    }

}
