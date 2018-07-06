package Actions;

public class MockCobolProcedure {

    public static void executeCobolProcedure(String[] data, int from, int to, long executingTime) {

        for (int i = from; i < to; i++) {
            data[i] = data[i].toUpperCase();
        }
    }
}
