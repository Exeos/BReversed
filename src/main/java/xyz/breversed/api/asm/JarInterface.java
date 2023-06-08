package xyz.breversed.api.asm;

import org.objectweb.asm.tree.ClassNode;

import java.util.ArrayList;
import java.util.Map;

public interface JarInterface {

    default ArrayList<ClassNode> getClasses() {
        return JarLoader.classes;
    }

    default Map<String, byte[]> getFiles() {
        return JarLoader.files;
    }
}