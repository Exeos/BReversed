package me.exeos.breversed.transformer.context;

import me.exeos.asmplus.jar.JarArchive;
import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.MethodNode;

public record MethodContext(ClassContext ownerCtx, MethodNode methodNode) {

    public JarArchive jar() {
        return ownerCtx.jar();
    }

    public ClassNode owner() {
        return ownerCtx.classNode();
    }
}
