import protocols.HDDtransferProtocol;
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
            case "HDD":
                transferProtocol = new HDDtransferProtocol();
                break;
            default:
                System.out.println("Wrong input arguments.\n"
                        + "Use TCP/HDD/SHM for protocol and producer/consumer for executor");
                return;
        }

        transferProtocol.execute(args[1]);
    }

}
