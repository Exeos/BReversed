package me.exeos.breversed.config;

import me.exeos.breversed.BReversed;
import me.exeos.breversed.transformer.AbstractTransformer;

import java.nio.file.Path;

public record Config(BReversed.Mode mode, Path input, Path output, Path libsDir, AbstractTransformer[] transformers) {
}
