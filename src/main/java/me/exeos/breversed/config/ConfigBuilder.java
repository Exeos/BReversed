package me.exeos.breversed.config;

import me.exeos.breversed.BReversed;
import me.exeos.breversed.transformer.AbstractTransformer;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Objects;

public class ConfigBuilder {

    private BReversed.Mode mode;
    private Path input;
    private Path output;
    private Path libsDir;
    private AbstractTransformer[] transformers;

    public ConfigBuilder mode(BReversed.Mode mode) {
        this.mode = mode;
        return this;
    }

    public ConfigBuilder input(Path path) {
        if (!Files.exists(path)) {
            throw new IllegalArgumentException("Input file doesn't exist: " + path);
        }

        input = path;
        return this;
    }

    public ConfigBuilder output(Path path) {
        output = path;
        return this;
    }

    public ConfigBuilder libsDir(Path path) {
        if (!Files.exists(path)) {
            throw new IllegalArgumentException("Library directory doesn't exist: " + path);
        }

        libsDir = path;
        return this;
    }

    public ConfigBuilder transformers(AbstractTransformer... transformers) {
        this.transformers = transformers;
        return this;
    }

    public Config build() {
        return new Config(
                Objects.requireNonNull(mode, "Mode not specified"),
                Objects.requireNonNull(input, "Input not specified"),
                Objects.requireNonNull(output, "Output not specified"),
                Objects.requireNonNull(libsDir, "Library directory not specified"),
                Objects.requireNonNull(transformers, "Transformers not specified")
        );
    }
}
