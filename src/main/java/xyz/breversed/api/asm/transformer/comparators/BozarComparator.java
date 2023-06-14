package xyz.breversed.api.asm.transformer.comparators;

import xyz.breversed.api.asm.transformer.Transformer;

import java.util.Comparator;

public class BozarComparator implements Comparator<Transformer> {
    @Override
    public int compare(Transformer t1, Transformer t2) {
        int index1 = getIndex(t1);
        int index2 = getIndex(t2);
        return Integer.compare(index1, index2);
    }

    private int getIndex(Transformer transformer) {
        return switch (transformer.getClass().getSimpleName()) {
            case "BozarNumber" -> 0;
            case "BozarConstantFlow" -> 1;
            case "BozarString" -> 2;
            case "BozarLightFlow" -> 3;
            default -> Integer.MAX_VALUE;
        };
    }
}