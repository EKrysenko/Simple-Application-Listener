package scheduler;

import java.io.*;

public class Scheduler {
    private String path;
    private int command;
    private int sizeCharArray;

    public Scheduler(String path, int sizeCharArray) {
        this.path = path;
        this.sizeCharArray = sizeCharArray;
    }

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

    public void sendMessage(int command) {

//        System.out.println("start sending in sendMessage()");
        try (BufferedWriter bw = new BufferedWriter(new FileWriter(path))) {
//            System.out.println("before write in sendMessage()");
            bw.write(String.valueOf(sizeCharArray * 10 + command));
            this.command = command;
        } catch (IOException e) {
            e.printStackTrace();
        }
//        System.out.println("stop sending in sendMessage()");
    }

    public int getSizeCharArray() {
        return sizeCharArray;
    }
}