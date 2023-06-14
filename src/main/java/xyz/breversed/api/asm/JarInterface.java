package xyz.breversed.api.asm;

import org.objectweb.asm.tree.ClassNode;

import java.util.ArrayList;
import java.util.HashMap;

public interface JarInterface {


    default ArrayList<ClassNode> getClasses() {
        return new ArrayList<>(JarLoader.classes);
    }

    default HashMap<String, byte[]> getFiles() {
        return JarLoader.files;
    }
}