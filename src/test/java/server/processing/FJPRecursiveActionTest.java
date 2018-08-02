package server.processing;

import server.parallelization.FJPRecursiveAction;
import org.junit.Assert;
import org.junit.Before;

public class FJPRecursiveActionTest {

    private FJPRecursiveAction action;
    private String[] actual;
    private String[] expected;

    @Before
    public void setup() {
        actual = new String[]{"a", "b", "c", "d", "e", "f", "g", "h", "i", "j", "k", "l"};
        expected = new String[]{"A", "B", "C", "D", "E", "F", "G", "H", "I", "J", "K", "L"};

        action = new FJPRecursiveAction(actual, 0, actual.length);
    }

    @org.junit.Test
    public void compute() {
        action.compute();
        Assert.assertArrayEquals(expected, actual);
    }
}