package scheduler;

import java.io.*;

public class Scheduler {
    private String path;
    private int command;

    public Scheduler(String path) {
        this.path = path;
    }

    public int getCommand() {
        try (BufferedReader br = new BufferedReader(new FileReader(path))) {
            command = Integer.valueOf(br.readLine());
        } catch (IOException e) {
            e.printStackTrace();
        }
        return command;
    }

    public void setCommand(int command) {
        try (BufferedWriter bw = new BufferedWriter(new FileWriter(path))) {
            bw.write(command);
            this.command = command;
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


}
