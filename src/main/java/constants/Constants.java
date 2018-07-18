package constants;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;

public class Constants {

    public static final int SIZE;
    public static final int DATA_SIZE = 4;
    public static String SHARED_MEMORY_PATH;
    public static String HDD_PATH;
    public static String RECEIVED_FILE;
    public static String SEND_FILE;
    public static String NAMED_PIPE;
    public static String TCP_HOST;
    public static int TCP_CONSUMER_PORT;
    public static int TCP_PRODUCER_PORT;
    public static int BLOCK_SIZE;
    public static int TRANSFER_TIME_IN_SECONDS;
    public static int UP_BOUNDER_IN_BYTES;
    public static int LOW_BOUNDER_IN_BYTES;


    static {
        Properties properties = new Properties();
        try {
            properties.load(new FileInputStream("./config.properties"));
        } catch (IOException e) {
            System.out.println("config.properties file is not found");
        }
        SIZE = Integer.parseInt(properties.getProperty("SIZE"));
        SHARED_MEMORY_PATH = properties.getProperty("SHARED_MEMORY_PATH");
        HDD_PATH = properties.getProperty("HDD_PATH");
        RECEIVED_FILE = properties.getProperty("RECEIVED_FILE");
        SEND_FILE = properties.getProperty("SEND_FILE");
        NAMED_PIPE = properties.getProperty("NAMED_PIPE");
        TCP_HOST = properties.getProperty("TCP_HOST");
        TCP_CONSUMER_PORT = Integer.parseInt(properties.getProperty("TCP_CONSUMER_PORT"));
        TCP_PRODUCER_PORT = Integer.parseInt(properties.getProperty("TCP_PRODUCER_PORT"));
        BLOCK_SIZE = Integer.parseInt(properties.getProperty("BLOCK_SIZE"));
        TRANSFER_TIME_IN_SECONDS = Integer.parseInt(properties.getProperty("TRANSFER_TIME_IN_SECONDS"));
        UP_BOUNDER_IN_BYTES = Integer.parseInt(properties.getProperty("UP_BOUNDER_IN_BYTES"));
        LOW_BOUNDER_IN_BYTES = Integer.parseInt(properties.getProperty("LOW_BOUNDER_IN_BYTES"));
    }

}
