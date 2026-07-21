package me.exeos.breversed.transformer.combined;

import me.exeos.asmplus.jar.JarArchive;
import me.exeos.breversed.transformer.AbstractTransformer;

public abstract class CombinedTransformer extends AbstractTransformer {

    private final AbstractTransformer[] transformers;

    public CombinedTransformer(AbstractTransformer... transformers) {
        this.transformers = transformers;
    }

    @Override
    public void transform(JarArchive jar) {
        for (AbstractTransformer transformer : transformers) {
            transformer.transform(jar);
        }
    }
}
