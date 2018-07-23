package protocols;

public interface TransferProtocol {

    default void execute(String executor, String lowSizePackage, String highSizePackage, String transferTimeInSeconds) {
        switch (executor) {
            case "producer":
                executeProducer(Integer.parseInt(lowSizePackage),
                        Integer.parseInt(highSizePackage),
                        Integer.parseInt(transferTimeInSeconds));
                break;
            case "consumer":
                executeConsumer();
                break;
        }
    }

    void executeProducer(int lowSizePackage, int highSizePackage, int transferTimeInSeconds);

    void executeConsumer();

}
