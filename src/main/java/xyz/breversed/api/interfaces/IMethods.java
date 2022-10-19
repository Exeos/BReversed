package xyz.breversed.api.interfaces;

import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.MethodNode;
import xyz.breversed.api.JarLoader;

import java.util.ArrayList;

public interface IMethods {

    default ArrayList<ClassNode> getClasses() {
        return JarLoader.classes;
    }

    default MethodNode getMethod(ClassNode classNode, String name, String desc) {
        return classNode.methods.stream().filter(methodNode -> methodNode.name.equals(name) && methodNode.desc.equals(desc)).findFirst().orElse(null);
    }

    default MethodNode getMethod(ClassNode classNode, String name, String desc, String signature) {
        return classNode.methods.stream().filter(methodNode -> methodNode.name.equals(name) && methodNode.desc.equals(desc) && methodNode.signature.equals(signature)).findFirst().orElse(null);
    }
}
