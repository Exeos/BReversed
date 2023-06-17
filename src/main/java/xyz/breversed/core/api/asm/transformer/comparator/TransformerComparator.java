package xyz.breversed.core.api.asm.transformer.comparator;

import xyz.breversed.core.api.asm.transformer.Transformer;

import java.util.Comparator;

public abstract class TransformerComparator implements Comparator<Transformer> {

    protected abstract int getIndex(Transformer transformer);

}
