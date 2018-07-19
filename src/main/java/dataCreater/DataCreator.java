package dataCreater;

import org.apache.commons.lang3.RandomStringUtils;

import java.util.ArrayList;
import java.util.List;

public class DataCreator {

    public static String createFixedSizePackage(int packageSize) {
        return RandomStringUtils.random(packageSize, "UTF-8");
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
