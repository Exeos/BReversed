package xyz.breversed.api.asm.transformer.comparator;

import xyz.breversed.api.asm.transformer.Transformer;

import java.util.Comparator;

public abstract class TransformerComparator implements Comparator<Transformer> {

    protected abstract int getIndex(Transformer transformer);

}
