import java.io.File;

public class CallingCOBOL {

    static {
        File cobolLibrary = new File("./src/main/resources/libadaptor.so");
        System.load(cobolLibrary.getAbsolutePath());
    }

    private native String adaptor(String from_java);

    public static void main(String[] args) {
        String str = new CallingCOBOL().adaptor("wapwapwapwap");
        System.out.println(str);
    }
}
