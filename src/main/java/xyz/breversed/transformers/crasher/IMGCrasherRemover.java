package xyz.breversed.transformers.crasher;

import org.objectweb.asm.tree.ClassNode;
import xyz.breversed.api.transformer.Transformer;
import xyz.breversed.api.utils.RenameUtil;

public class IMGCrasherRemover extends Transformer {

    @Override
    protected void transform() {
        for (ClassNode classNode : getClasses()) {
            if (classNode.name.toLowerCase().contains("<html><img src="))
                RenameUtil.rename(classNode);
        }
        for (String s : getFiles().keySet()) {
            if (s.toLowerCase().contains("<html><img src="))
                getFiles().put(RenameUtil.rename(), getFiles().remove(s));
        }
    }
}
