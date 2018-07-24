package factories;

import consumers.Consumer;
import consumers.SHMConsumer;
import producers.Producer;
import producers.SHMProducer;

public class SHMFactory implements TransferProtocolFactory {

    @Override
    public Consumer createConsumer() {
        return new SHMConsumer();
    }

    @Override
    public Producer createProducer(int lowSizePackage, int highSizePackage, int transferTimeInSeconds) {
        return new SHMProducer(lowSizePackage, highSizePackage, transferTimeInSeconds);
    }

}
