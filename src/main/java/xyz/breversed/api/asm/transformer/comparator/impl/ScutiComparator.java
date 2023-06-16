package xyz.breversed.api.asm.transformer.comparator.impl;

import xyz.breversed.api.asm.transformer.Transformer;
import xyz.breversed.api.asm.transformer.comparator.CompTarget;

import java.util.Comparator;

@CompTarget(target = "scuti")
public class ScutiComparator implements Comparator<Transformer> {
    @Override
    public int compare(Transformer t1, Transformer t2) {
        int index1 = getIndex(t1);
        int index2 = getIndex(t2);
        return Integer.compare(index1, index2);
    }

    private int getIndex(Transformer transformer) {
        return switch (transformer.getClass().getSimpleName()) {
            case "ScutiNumber" -> 0;
            case "ScutiFlow" -> 1;
            case "ScutiInvokeDynamic" -> 2;
            case "ScutiFastString", "ScutiStrongString" -> 3;
            case "ScutiClassEncrypt" -> 4;
            default -> Integer.MAX_VALUE;
        };
    }
}