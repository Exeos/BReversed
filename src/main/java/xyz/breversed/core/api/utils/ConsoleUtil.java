package xyz.breversed.core.api.utils;

import lombok.experimental.UtilityClass;

@UtilityClass
public class ConsoleUtil {

    private String task;
    private int taskIndex = 1;

    public void start(String task) {
        ConsoleUtil.task = task;
        println("Starting task " + taskIndex + ": " + task);
    }

    public void finish() {
        println("Finished task " + taskIndex + ": " + task);
        println();
        taskIndex++;
    }

    public void finishNoLine() {
        println("Finished task " + taskIndex + ": " + task);
        taskIndex++;
    }

    public void println() {
        println("");
    }

    public void println(Object line) {
        // TODO send to gui console when connected
        System.out.println(line);
    }
}
