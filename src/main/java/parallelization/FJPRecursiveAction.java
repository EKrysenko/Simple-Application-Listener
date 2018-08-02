package parallelization;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ForkJoinTask;
import java.util.concurrent.RecursiveAction;

import static processing.MockCobolWrapperProcedure.executeCobolProcedure;

public class FJPRecursiveAction extends RecursiveAction {

    private static final int THRESHOLD = 4;

    private String[] data;
    private int from;
    private int to;

    public FJPRecursiveAction(String[] data, int from, int to) {
        this.data = data;
        this.from = from;
        this.to = to;
    }

    @Override
    public void compute() {

        if (to - from < THRESHOLD) {
            processing(data, from, to);

        } else {
            ForkJoinTask.invokeAll(createSubtasks());
        }
    }

    private List<FJPRecursiveAction> createSubtasks() {

        List<FJPRecursiveAction> subtasks = new ArrayList<>();

        int mid = (to + from) / 2;

        subtasks.add(new FJPRecursiveAction(data, from, mid));
        subtasks.add(new FJPRecursiveAction(data, mid, to));

        return subtasks;
    }

    private void processing(String[] data, int from, int to) {
        executeCobolProcedure(data, from, to);
    }

}
