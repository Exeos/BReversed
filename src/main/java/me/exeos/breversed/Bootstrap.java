package me.exeos.breversed;

import me.exeos.breversed.config.ConfigBuilder;

import java.nio.file.Path;

public class Bootstrap {

    static void main(String[] args) {
        new BReversed(new ConfigBuilder()
                .mode(BReversed.Mode.TRANSFORM)
                .input(Path.of("./work/in.jar"))
                .output(Path.of("./work/out.jar"))
                .libsDir(Path.of("./work/libs"))
                .transformers(

                )
                .build()
        ).run();
    }
}