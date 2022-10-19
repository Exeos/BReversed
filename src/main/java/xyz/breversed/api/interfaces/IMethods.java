package xyz.breversed.api.interfaces;

import org.objectweb.asm.tree.ClassNode;
import xyz.breversed.api.JarLoader;

import java.util.ArrayList;

public interface IMethods {

    default ArrayList<ClassNode> getClasses() {
        return JarLoader.classes;
    }
}
