package xyz.breversed.core.api.asm.utils;

import lombok.experimental.UtilityClass;
import org.objectweb.asm.tree.ClassNode;
import xyz.breversed.core.BReversed;
import xyz.breversed.core.api.asm.JarInterface;

@UtilityClass
public class RenameUtil {

    private int index = 0;

    public String rename() {
        return BReversed.INSTANCE.config.renamerStr + index++;
    }

    public void rename(ClassNode classNode) {
        BReversed.INSTANCE.jarLoader.classes.get(classNode.name).name = rename();
    }
}
