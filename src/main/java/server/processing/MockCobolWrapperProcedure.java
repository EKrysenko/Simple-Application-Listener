package server.processing;

public class MockCobolWrapperProcedure {

    public static void executeCobolProcedure(String[] data, int from, int to) {

        for (int i = from; i < to; i++) {
            data[i] = data[i].toUpperCase();
        }
    }
}
