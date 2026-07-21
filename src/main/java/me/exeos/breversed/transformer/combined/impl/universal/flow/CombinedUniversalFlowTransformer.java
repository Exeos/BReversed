package me.exeos.breversed.transformer.combined.impl.universal.flow;

import me.exeos.breversed.transformer.combined.CombinedTransformer;
import me.exeos.breversed.transformer.impl.universal.flow.BlockRearranger;
import me.exeos.breversed.transformer.impl.universal.flow.DeadBlockRemover;
import me.exeos.breversed.transformer.impl.universal.flow.IndirectJumpRemover;
import me.exeos.breversed.transformer.impl.universal.flow.UselessJumpRemover;

public class CombinedUniversalFlowTransformer extends CombinedTransformer {

    public CombinedUniversalFlowTransformer() {
        super(
                new DeadBlockRemover(),
                new IndirectJumpRemover(),
                new DeadBlockRemover(),
                new BlockRearranger(),
                new UselessJumpRemover()
        );
    }
}
