package xyz.breversed.transformers.bozar;

import me.exeos.asmplus.JarLoader;
import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.MethodNode;
import xyz.breversed.BReversed;
import xyz.breversed.api.asm.transformer.Transformer;

public class BozarCrasher extends Transformer {
    @Override
    protected void transform() {
        for (ClassNode classNode : getClasses()) {
            if (classNode.methods.size() == 1) {
                MethodNode methodNode = classNode.methods.get(0);
                if (methodNode.name.equals("\u0001") && methodNode.desc.equals("(\u0001/)L\u0001/;") && methodNode.access <= 100)
                    removeClass(classNode);
            }
        }
    }
}
