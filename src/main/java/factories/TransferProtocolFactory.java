package factories;

import consumers.Consumer;
import producers.Producer;

public interface TransferProtocolFactory {

    Consumer createConsumer();

    Producer createProducer(int lowSizePackage, int highSizePackage, int transferTimeInSeconds);

}
