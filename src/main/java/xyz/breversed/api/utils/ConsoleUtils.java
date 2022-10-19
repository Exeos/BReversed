package xyz.breversed.api.utils;

import lombok.experimental.UtilityClass;

@UtilityClass
public class ConsoleUtils {

    private String task;
    private int taskIndex = 1;

    public void start(String task) {
        ConsoleUtils.task = task;
        System.out.println("Starting task " + taskIndex + ": " + task);
    }

    public void finish() {
        System.out.println("Finished task " + taskIndex + ": " + task);
        System.out.println();
        taskIndex++;
    }

    public void finishNoLine() {
        System.out.println("Finished task " + taskIndex + ": " + task);
        taskIndex++;
    }
}
