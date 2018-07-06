package interfaces;

public interface TransferProtocol {


    default void execute(String executor) {
        switch (executor) {
            case "producer":
                executeProducer();
                break;
            case "consumer":
                executeConsumer();
                break;
        }
    }

    void executeProducer();

    void executeConsumer();

}
