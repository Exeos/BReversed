package xyz.breversed.api.asm.transformer.comparator.impl;

import xyz.breversed.api.asm.transformer.Transformer;
import xyz.breversed.api.asm.transformer.comparator.CompTarget;

import java.util.Comparator;

@CompTarget(target = "bozar")
public class BozarComparator implements Comparator<Transformer> {
    @Override
    public int compare(Transformer t1, Transformer t2) {
        int index1 = getIndex(t1);
        int index2 = getIndex(t2);
        return Integer.compare(index1, index2);
    }

    private int getIndex(Transformer transformer) {
        return switch (transformer.getClass().getSimpleName()) {
            case "BozarCrasher" -> 0;
            case "BozarNumber" -> 1;
            case "BozarConstantFlow" -> 2;
            case "BozarString" -> 3;
            case "BozarHeavyFlow" -> 4;
            case "BozarLightFlow" -> 5;
            default -> Integer.MAX_VALUE;
        };
    }
}