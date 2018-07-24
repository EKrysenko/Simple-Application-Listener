package schedulers;

import java.io.*;

public class NamedPipeScheduler implements Scheduler {
    private String path;
    private int command;
    private int sizeCharArray;

    public NamedPipeScheduler(String path, int sizeCharArray) {
        this.path = path;
        this.sizeCharArray = sizeCharArray;
    }

    public NamedPipeScheduler(String path) {
        this.path = path;
    }

    @Override
    public int getCommand() {

        try (BufferedReader br = new BufferedReader(new FileReader(path))) {
            int message = Integer.parseInt(br.readLine());
            command = message % 10;
            sizeCharArray = message / 10;
        } catch (IOException e) {
            e.printStackTrace();
        }

        return command;
    }

    @Override
    public void sendMessage(int command) {

        try (BufferedWriter bw = new BufferedWriter(new FileWriter(path))) {
            bw.write(String.valueOf(sizeCharArray * 10 + command));
            this.command = command;
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public int getSizeCharArray() {
        return sizeCharArray;
    }
}