package server.processing;

public class CobolExecutor {

    public static void executeCobolProcedure(String[] data, int from, int to) {
        CallingCobol procedure = new CallingCobol();

        for (int i = from; i < to; i++) {
            data[i] = procedure.adaptor(data[i]);
        }
    }
}
