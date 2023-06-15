package xyz.breversed.api.asm.transformer;

import org.reflections.Reflections;
import org.reflections.scanners.SubTypesScanner;
import xyz.breversed.api.asm.transformer.comparator.CompTarget;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;

public class TransformerManager {

    public final HashMap<String, ArrayList<Transformer>> transformerMap = new HashMap<>();
    public final ArrayList<Transformer> transformers = new ArrayList<>();

    public void transform() {
        sortAndAdd();
        for (Transformer transformer : transformers) {
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

    private void sortAndAdd() {
        Reflections reflections = new Reflections("xyz.breversed.api.asm.transformer.comparator.impl", new SubTypesScanner(false));
        for (Class<?> aClass : reflections.getSubTypesOf(Object.class)) {
            if (aClass.getAnnotation(CompTarget.class) == null)
                continue;
            try {
                String target = aClass.getAnnotation(CompTarget.class).target();
                System.out.println(target);
                transformerMap.get(target).sort((Comparator<Transformer>) aClass.newInstance());
            } catch (InstantiationException | IllegalAccessException e) {
                System.out.println("Error sorting transformers");
                e.printStackTrace();
            }
        }

        for (String p : transformerMap.keySet()) {
            transformers.addAll(transformerMap.get(p));
        }
    }
}