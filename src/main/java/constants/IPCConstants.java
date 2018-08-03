package constants;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;

public class IPCConstants {

    public static final int SIZE;
    public static final String SHARED_MEMORY_PATH;
    public static final int BLOCK_SIZE;
    public static final int ARRAY_SIZE_IN_PACKAGES;
    public static final int CLEAR_UTIL;
    public static final int DATA_AREA_START;
    public static final int SERVER_OFFSET;
    public static final int CLIENT_OFFSET;

    static {
        Properties properties = new Properties();
        try {
            properties.load(new FileInputStream("./ipc.properties"));
        } catch (IOException e) {
            System.out.println("ipc.properties file is not found");
        }
        SIZE = Integer.parseInt(properties.getProperty("SIZE"));
        SHARED_MEMORY_PATH = properties.getProperty("SHARED_MEMORY_PATH");
        BLOCK_SIZE = Integer.parseInt(properties.getProperty("BLOCK_SIZE"));
        ARRAY_SIZE_IN_PACKAGES = Integer.parseInt(properties.getProperty("ARRAY_SIZE_IN_PACKAGES"));
        DATA_AREA_START = Integer.parseInt(properties.getProperty("DATA_AREA_START"));
        SERVER_OFFSET = Integer.parseInt(properties.getProperty("SERVER_OFFSET"));
        CLIENT_OFFSET = Integer.parseInt(properties.getProperty("CLIENT_OFFSET"));
        CLEAR_UTIL = Integer.parseInt(properties.getProperty("CLEAR_UTIL"));
    }

}
