package xyz.breversed.api.asm.transformer;

import org.reflections.Reflections;
import org.reflections.scanners.SubTypesScanner;

import java.util.ArrayList;
import java.util.Comparator;

public class TransformerManager {

    public final ArrayList<Transformer> transformers = new ArrayList<>();

    public void transform() {

        sort();
        for (Transformer transformer : transformers) {
            System.out.println(transformer.getClass().getSimpleName());
            if (true)
                continue;
            System.out.println("Running " + transformer + " ");
            try {
                transformer.transform();
                System.out.println("Success!");
            } catch (Exception e) {
                System.out.println("Error running " + transformer + " transformer:");
                e.printStackTrace();
            }
        }
    }

    private void sort() {
        Reflections reflections = new Reflections("xyz.breversed.api.asm.transformer.comparators", new SubTypesScanner(false));
        for (Class<?> aClass : reflections.getSubTypesOf(Object.class)) {
            try {
                transformers.sort((Comparator<Transformer>) aClass.newInstance());
            } catch (InstantiationException | IllegalAccessException e) {
                System.out.println("Error sorting transformers");
                e.printStackTrace();
            }
        }
    }

    public boolean contains(Class<? extends Transformer>  t) {
        return transformers.stream().filter(transformer -> transformer.getClass() == t).findFirst().orElse(null) != null;
    }
}
