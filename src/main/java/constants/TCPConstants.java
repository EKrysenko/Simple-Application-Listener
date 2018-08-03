package constants;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;

public class TCPConstants {

    public static String TCP_HOST;
    public static int TCP_SERVER_PORT;
    public static int TCP_CLIENT_PORT;
    public static int BLOCK_SIZE;
    public static int ARRAY_SIZE_IN_PACKAGES;

    static {
        Properties properties = new Properties();
        try {
            properties.load(new FileInputStream("./tcp.properties"));
        } catch (IOException e) {
            System.out.println("tcp.properties file is not found");
        }
        TCP_HOST = properties.getProperty("TCP_HOST");
        TCP_SERVER_PORT = Integer.parseInt(properties.getProperty("TCP_SERVER_PORT"));
        TCP_CLIENT_PORT = Integer.parseInt(properties.getProperty("TCP_CLIENT_PORT"));
        BLOCK_SIZE = Integer.parseInt(properties.getProperty("BLOCK_SIZE"));
        ARRAY_SIZE_IN_PACKAGES = Integer.parseInt(properties.getProperty("ARRAY_SIZE_IN_PACKAGES"));
    }

}
