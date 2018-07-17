public class CallingCOBOL {

    static {
        System.load("/home/egor/IdeaProjects/lgi-hrwd/libadaptor.so");
    }

    private native void adaptor(String from_java);

    public static void main(String[] args) {
        new CallingCOBOL().adaptor("Hello world!");
    }
}
