package xyz.breversed.core.transformers.other.crack;

import org.objectweb.asm.tree.ClassNode;
import xyz.breversed.core.BReversed;
import xyz.breversed.core.api.asm.transformer.Transformer;

import java.util.HashMap;

public class DogeRepacker extends Transformer {

    @Override
    protected void transform() {
        HashMap<String, ClassNode> renamed = new HashMap<>();
        for (ClassNode classNode : getClasses()) {
            renamed.put(classNode.name, classNode);
        }
        System.out.println("Repacked " + renamed.size() + " classes");

        BReversed.INSTANCE.jarLoader.classes = renamed;
    }
}
