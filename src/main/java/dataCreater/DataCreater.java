package dataCreater;

import java.io.UnsupportedEncodingException;
import java.util.Arrays;

public class DataCreater {
    private static final String NAME = " Ivanov Ivan Ivanovich ";
    private static final int ONE_ENTRY_CAPACITY = 200;

    public static String create(double capacityInKilobytes) throws UnsupportedEncodingException {
        StringBuilder stringBuilder = new StringBuilder();
        byte[] initBytes;
        byte[] additionaryBytes;
        String string;
        for (int i = 0; i < capacityInKilobytes * Math.pow(2, 10) / ONE_ENTRY_CAPACITY; i++) {
            string = i + NAME;
            initBytes = string.getBytes("UTF-8");
            if (ONE_ENTRY_CAPACITY > initBytes.length) {
                additionaryBytes = Arrays.copyOf(initBytes, ONE_ENTRY_CAPACITY);
                for (int j = initBytes.length; j < additionaryBytes.length; j++) {
                    additionaryBytes[j] = (byte) (48 + (Math.random() * (57 - 48)));
                }
                stringBuilder.append(new String(additionaryBytes, "UTF-8")).append("\n");
            }
        }
        return stringBuilder.toString();
    }
}
