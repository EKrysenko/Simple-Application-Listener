package processing;

import java.io.File;

public class CallingCobol {
    static {
        File cobolLibrary = new File("./src/main/resources/libadaptor.so");
        System.load(cobolLibrary.getAbsolutePath());
    }

    public native String adaptor(String from_java);
}
