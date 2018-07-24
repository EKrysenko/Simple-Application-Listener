package factories;

import consumers.Consumer;
import consumers.TCPConsumer;
import producers.Producer;
import producers.TCPProducer;

public class TCPFactory implements TransferProtocolFactory {

    @Override
    public Consumer createConsumer() {
        return new TCPConsumer();
    }

    @Override
    public Producer createProducer(int lowSizePackage, int highSizePackage, int transferTimeInSeconds) {
        return new TCPProducer(lowSizePackage, highSizePackage, transferTimeInSeconds);
    }
}