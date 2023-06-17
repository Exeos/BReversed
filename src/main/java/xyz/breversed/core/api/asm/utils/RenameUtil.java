package xyz.breversed.core.api.asm.utils;

import lombok.experimental.UtilityClass;
import org.objectweb.asm.tree.ClassNode;
import xyz.breversed.core.BReversed;
import xyz.breversed.core.api.asm.JarInterface;
import xyz.breversed.core.api.asm.JarLoader;

@UtilityClass
public class RenameUtil implements JarInterface {

    private int index = 0;

    public String rename() {
        return BReversed.INSTANCE.config.renamerStr + index++;
    }

    public void rename(ClassNode classNode) {
        JarLoader.classes.get(JarLoader.classes.indexOf(classNode)).name = rename();
    }
}
