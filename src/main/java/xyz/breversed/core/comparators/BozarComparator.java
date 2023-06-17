package xyz.breversed.core.comparators;

import xyz.breversed.core.api.asm.transformer.Transformer;
import xyz.breversed.core.api.asm.transformer.comparator.CompTarget;
import xyz.breversed.core.api.asm.transformer.comparator.TransformerComparator;

@CompTarget(target = "bozar")
public class BozarComparator extends TransformerComparator {

    @Override
    public int compare(Transformer t1, Transformer t2) {
        int index1 = getIndex(t1);
        int index2 = getIndex(t2);
        return Integer.compare(index1, index2);
    }

    @Override
    protected int getIndex(Transformer transformer) {
        return switch (transformer.getClass().getSimpleName()) {
            case "BozarCrasher" -> 0;
            case "BozarNumber" -> 1;
            case "BozarConstantFlow" -> 2;
            case "BozarString" -> 3;
            case "BozarHeavyFlow", "BozarLightFlow" -> 4;
            default -> Integer.MAX_VALUE;
        };
    }
}