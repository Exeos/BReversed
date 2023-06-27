package xyz.breversed.core.transformers.crasher;

import org.objectweb.asm.tree.ClassNode;
import xyz.breversed.core.api.asm.transformer.Transformer;
import xyz.breversed.core.api.asm.utils.RenameUtil;

public class IMGCrasherRemover extends Transformer {

    @Override
    protected void transform() {
        for (ClassNode classNode : getClasses()) {
            if (classNode.name.toLowerCase().contains("<html><img src="))
                RenameUtil.rename(classNode);
        }
        for (String s : getResources().keySet()) {
            if (s.toLowerCase().contains("<html><img src="))
                getResources().put(RenameUtil.rename(), getResources().remove(s));
        }
    }
}
