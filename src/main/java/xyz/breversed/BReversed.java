package xyz.breversed;

import xyz.breversed.api.Config;
import xyz.breversed.api.JarLoader;
import xyz.breversed.api.detection.Detector;
import xyz.breversed.api.transformer.TransformerManager;
import xyz.breversed.api.utils.ConsoleUtils;

public enum BReversed {

    INSTANCE;

    private final String[] authors = new String[] { "Exeos" };
    private final String version = "v1.0.0";

    public final Config config = new Config();
    public final TransformerManager transformerManager = new TransformerManager();
    private final Detector detector = new Detector();

    public static void main(String[] args) {
        BReversed.INSTANCE.start();
    }

    public void start() {
        System.out.println("""
                 ____  _____                                  _\s
                |  _ \\|  __ \\                                | |
                | |_) | |__) |_____   _____ _ __ ___  ___  __| |
                |  _ <|  _  // _ \\ \\ / / _ \\ '__/ __|/ _ \\/ _` |
                | |_) | | \\ \\  __/\\ V /  __/ |  \\__ \\  __/ (_| |
                |____/|_|  \\_\\___| \\_/ \\___|_|  |___/\\___|\\__,_|""");
        System.out.println("by " + getAuthorsFormatted());

        {
            System.out.println("______________________________________________________________");
            ConsoleUtils.start("Loading Config");
            try {
                config.loadConfig();
                ConsoleUtils.finish();
            } catch (Exception e) {
                System.out.println("Error loading config:");
                e.printStackTrace();
                return;
            }

            ConsoleUtils.start("Loading jar");
            JarLoader.loadJar();
            ConsoleUtils.finish();

            switch (config.task) {
                case DETECT -> {
                    ConsoleUtils.start("Detecting obfuscation");
                    detector.detect();
                }
                case TRANSFORM -> {
                    ConsoleUtils.start("Deobfuscation");
                    transformerManager.transform();
                    ConsoleUtils.finish();

                    ConsoleUtils.start("Exporting jar");
                    JarLoader.exportJar();
                }
            }
            ConsoleUtils.finishNoLine();
            System.out.println("______________________________________________________________");
        }

        System.out.println();
        System.out.println("BReversed " + version + " by " + getAuthorsFormatted());
    }

    private String getAuthorsFormatted() {
        final StringBuilder builder = new StringBuilder();
        for (int i = 0; i < authors.length; i++)
            builder.append(authors[i]).append((i + 1 == authors.length ? "" : (i + 2 >= authors.length ? " and " : ", ")));

        return builder.toString();
    }
}
