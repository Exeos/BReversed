package xyz.breversed.api.asm;

import org.objectweb.asm.tree.ClassNode;
import xyz.breversed.BReversed;

import java.util.ArrayList;
import java.util.HashMap;

public interface JarInterface {

    default void removeClass(ClassNode classNode) {
        BReversed.INSTANCE.jarLoader.classes.remove(classNode.name);
    }

    default void removeResource(String name) {
        BReversed.INSTANCE.jarLoader.resources.remove(name);
    }

    default ArrayList<ClassNode> getClasses() {
        return new ArrayList<>(BReversed.INSTANCE.jarLoader.classes.values());
    }

    default HashMap<String, byte[]> getResources() {
        return BReversed.INSTANCE.jarLoader.resources;
    }

    default ClassNode getClass(String name) {
        return BReversed.INSTANCE.jarLoader.classes.get(name);
    }

    default byte[] getResource(String name) {
        return BReversed.INSTANCE.jarLoader.resources.get(name);
    }
}