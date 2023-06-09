package xyz.breversed.api.asm.transformer;

import java.util.ArrayList;

public class TransformerManager {

    public final ArrayList<Transformer> transformers = new ArrayList<>();

    public void transform() {
        transformers.forEach(transformer -> {
            System.out.println("Running " + transformer.toString() + " ");
            try {
                transformer.transform();
                System.out.println("Success!");
            } catch (Exception e) {
                System.out.println("Error running " + transformer + " transformer:");
                e.printStackTrace();
            }
        });
    }
}
