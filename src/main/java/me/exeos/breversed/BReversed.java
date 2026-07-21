package me.exeos.breversed;

import me.exeos.asmplus.jar.JarArchive;
import me.exeos.asmplus.jar.JarLoader;
import me.exeos.breversed.config.Config;
import me.exeos.breversed.transformer.AbstractTransformer;

import java.io.FileOutputStream;
import java.io.IOException;
import java.util.function.Supplier;

public class BReversed {

    private final Config config;

    private final JarArchive jar;

    public BReversed(Config config) {
        this.config = config;
        try {
            jar = JarLoader.load(config.input().toFile(), config.libsDir().toFile());
        } catch (IOException e) {
            throw new RuntimeException("Failed to load JarArchive from input", e);
        }
    }

    public void run() {
        for (AbstractTransformer transformer : config.transformers()) {
            transformer.transform(jar);
        }

        export();
    }

    private void export() {
        try {
            JarLoader.export(jar, new FileOutputStream(config.output().toFile()), true);
        } catch (IOException e) {
            throw new RuntimeException("Failed to export JarArchive to output file", e);
        }
    }

    public enum Mode {
        DETECT,
        TRANSFORM
    }
}
