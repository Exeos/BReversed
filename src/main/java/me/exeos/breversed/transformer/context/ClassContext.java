package me.exeos.breversed.transformer.context;

import me.exeos.asmplus.jar.JarArchive;
import org.objectweb.asm.tree.ClassNode;

public record ClassContext(JarArchive jar, ClassNode classNode) {
}
