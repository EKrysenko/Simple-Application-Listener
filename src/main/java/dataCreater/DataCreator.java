package dataCreater;

import org.apache.commons.lang3.RandomStringUtils;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;


public class DataCreator {

    private static final String NAME = " Ivanov Ivan Ivanovich ";
    private static final int ONE_ENTRY_CAPACITY = 100;

    public static String create(double capacityInKilobytes) throws UnsupportedEncodingException {

        StringBuilder stringBuilder = new StringBuilder();

        byte[] initBytes;
        byte[] additionaryBytes;
        String string;

        for (int i = 0; i < capacityInKilobytes * Math.pow(2, 10) / ONE_ENTRY_CAPACITY; i++) {
            string = i + NAME;
            initBytes = string.getBytes("UTF-8");
            if (ONE_ENTRY_CAPACITY > initBytes.length) {
                additionaryBytes = Arrays.copyOf(initBytes, ONE_ENTRY_CAPACITY - 1);
                for (int j = initBytes.length; j < additionaryBytes.length; j++) {
                    additionaryBytes[j] = (byte) (48 + (Math.random() * (57 - 48)));
                }
                stringBuilder.append(new String(additionaryBytes, "UTF-8")).append("\n");
            }
        }

        return stringBuilder.toString();
    }

    public static List<String> createRandomSizePackage(double capacityInKilobytes, int lowBounderInBytes, int upBounderInBytes) {
        List<String> out = new ArrayList<>();
        byte[] allData = new byte[0];
        try {
            allData = create(capacityInKilobytes).getBytes("UTF-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        int allDataLength = allData.length;
        int startOfPackagePointer = 0;
        int endOfPackagePointer;
        int packageLength;
        while (startOfPackagePointer < allDataLength) {

            packageLength = (int) (Math.random() * (upBounderInBytes - lowBounderInBytes) + lowBounderInBytes);
            endOfPackagePointer = startOfPackagePointer + packageLength;

            if (endOfPackagePointer < allDataLength) {
                out.add(new String(allData, startOfPackagePointer, packageLength));
            } else if (allDataLength - startOfPackagePointer > lowBounderInBytes) {
                out.add(new String(allData, startOfPackagePointer, allDataLength - startOfPackagePointer));
            } else break;

            startOfPackagePointer = endOfPackagePointer;
        }
        return out;
    }

    public static List<String> createRandomSizePackage(int arraySize, int lowBounderInBytes, int upBounderInBytes) {
        List<String> out = new ArrayList<>(arraySize);
        for (int i = 0; i < arraySize; i++) {
            String string = RandomStringUtils.random((int) (Math.random() * (upBounderInBytes - lowBounderInBytes) + lowBounderInBytes), "UTF-8");
            out.add(string);
        }
        return out;
    }
}
