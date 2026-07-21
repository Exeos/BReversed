package me.exeos.breversed;

import me.exeos.breversed.config.ConfigBuilder;
import me.exeos.breversed.transformer.impl.bytus.BytusFlowUnflatten;
import me.exeos.breversed.transformer.impl.universal.BlockRearranger;
import me.exeos.breversed.transformer.impl.universal.DeadBlockRemover;
import me.exeos.breversed.transformer.impl.universal.IndirectJumpRemover;
import me.exeos.breversed.transformer.impl.universal.UselessJumpRemover;

import java.nio.file.Path;

public class Bootstrap {

    static void main(String[] args) {
        new BReversed(new ConfigBuilder()
                .mode(BReversed.Mode.TRANSFORM)
                .input(Path.of("./work/in.jar"))
                .output(Path.of("./work/out.jar"))
                .libsDir(Path.of("./work/libs"))
                .transformers(
                        new BytusFlowUnflatten(),
                        new DeadBlockRemover(),
                        new IndirectJumpRemover(),
                        new UselessJumpRemover(),
                        new BlockRearranger()
                )
                .build()
        ).run();
    }
}