import protocols.TransferProtocol;
import protocols.CQtransferProtocol;
import protocols.HDDtransferProtocol;
import protocols.SHMtransferProtocol;
import protocols.TCPtransferProtocol;

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
            case "CQ":
                transferProtocol = new CQtransferProtocol();
                break;
            default:
                System.out.println("Wrong input arguments.\n"
                        + "Use TCP/HDD/SHM for protocol and producer/consumer for executor");
                return;
        }

        transferProtocol.execute(args[1]);
    }

}
