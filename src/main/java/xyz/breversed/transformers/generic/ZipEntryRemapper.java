package xyz.breversed.transformers.generic;

import org.objectweb.asm.tree.ClassNode;
import xyz.breversed.BReversed;
import xyz.breversed.api.asm.transformer.Transformer;

import java.util.HashMap;

public class ZipEntryRemapper extends Transformer {

    @Override
    protected void transform() {
        HashMap<String, ClassNode> renamed = new HashMap<>();
        for (ClassNode classNode : getClasses()) {
            renamed.put(classNode.name, classNode);
        }
        System.out.println("Remapped " + renamed.size() + " classes");

        BReversed.INSTANCE.jarLoader.classes = renamed;
    }
}
