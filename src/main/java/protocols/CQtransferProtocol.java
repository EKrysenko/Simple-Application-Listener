package protocols;

import dataCreater.DataCreater;
import net.openhft.chronicle.queue.ExcerptAppender;
import net.openhft.chronicle.queue.ExcerptTailer;
import net.openhft.chronicle.queue.impl.single.SingleChronicleQueue;
import net.openhft.chronicle.queue.impl.single.SingleChronicleQueueBuilder;

import java.io.UnsupportedEncodingException;

import static constants.Constants.SHARED_MEMORY_PATH;

public class CQtransferProtocol implements TransferProtocol {
    String sendFile;

    {
        try {
            sendFile = DataCreater.create(50);
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
    }

    SingleChronicleQueue queue = SingleChronicleQueueBuilder.binary(SHARED_MEMORY_PATH).build();

    @Override
    public void executeProducer() {
        ExcerptAppender appender = queue.acquireAppender();
        System.out.println(sendFile.length());
        appender.writeText(sendFile);
    }

    @Override
    public void executeConsumer() {
        ExcerptTailer tailer = queue.createTailer();
        while (true) {
            String receivedText = tailer.readText();
            if (receivedText != null) {
                System.out.println(receivedText.length());
            }
        }
    }
}
