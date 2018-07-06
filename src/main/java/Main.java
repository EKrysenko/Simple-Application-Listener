import interfaces.TransferProtocol;
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
            default:
                System.out.println("Wrong input arguments. Should be %ProtocolName%, %ExecutorName%");
                return;
        }

        transferProtocol.execute(args[1]);
    }

}
