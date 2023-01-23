package xyz.breversed.api.utils;

import lombok.experimental.UtilityClass;
import org.objectweb.asm.tree.ClassNode;
import xyz.breversed.BReversed;
import xyz.breversed.api.JarLoader;

import java.util.ArrayList;

@UtilityClass
public class RenameUtil {

    private int index = 0;

    public String rename() {
        return BReversed.INSTANCE.config.renamerStr + index++;
    }

    public void rename(ClassNode classNode) {
        JarLoader.classes.get(JarLoader.classes.indexOf(classNode)).name = rename();
    }
}
