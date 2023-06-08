package xyz.breversed.api.asm.pattern.result;

import lombok.AllArgsConstructor;
import org.objectweb.asm.tree.AbstractInsnNode;
import org.objectweb.asm.tree.MethodNode;

import java.util.List;

@AllArgsConstructor
public class MethodResult {

    public final MethodNode methodNode;
    public final List<AbstractInsnNode> foundPatterns;
}