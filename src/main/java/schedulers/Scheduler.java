package schedulers;

public interface Scheduler {
    int getCommand();

    void sendMessage(int command);

    int getSizeCharArray();
}
