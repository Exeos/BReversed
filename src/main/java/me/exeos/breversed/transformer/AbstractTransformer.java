package me.exeos.breversed.transformer;

import me.exeos.asmplus.jar.JarArchive;
import me.exeos.breversed.transformer.context.ClassContext;
import me.exeos.breversed.transformer.context.MethodContext;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.MethodNode;

public abstract class AbstractTransformer implements Opcodes {

    public void transform(JarArchive jar) {
        for (ClassNode classNode : jar.getClasses().values()) {
            transform(new ClassContext(jar, classNode));
        }
    }

    public void transform(ClassContext context) {
        for (MethodNode methodNode : context.classNode().methods) {
            transform(new MethodContext(context, methodNode));
        }
    }

    public void transform(MethodContext context) {
        throw new IllegalStateException("MethodContext level transform needs to be implemented, if called");
    }
}
