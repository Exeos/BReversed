package xyz.breversed;

import xyz.breversed.api.Config;
import xyz.breversed.api.asm.JarLoader;
import xyz.breversed.api.asm.detection.Detector;
import xyz.breversed.api.asm.transformer.TransformerManager;
import xyz.breversed.api.utils.ConsoleUtil;

public enum BReversed {

    INSTANCE;

    private final String[] authors = new String[] { "Exeos", "$kush" };
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
            ConsoleUtil.start("Loading Config");
            try {
                config.loadConfig();
                ConsoleUtil.finish();
            } catch (Exception e) {
                System.out.println("Error loading config:");
                e.printStackTrace();
                return;
            }

            ConsoleUtil.start("Loading jar");
            JarLoader.loadJar();
            ConsoleUtil.finish();

            switch (config.task) {
                case DETECT -> {
                    ConsoleUtil.start("Detecting obfuscation");
                    detector.detect();
                }
                case TRANSFORM -> {
                    ConsoleUtil.start("Deobfuscation");
                    transformerManager.transform();
                    ConsoleUtil.finish();

                    ConsoleUtil.start("Exporting jar");
                    JarLoader.exportJar();
                }
            }
            ConsoleUtil.finishNoLine();
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
