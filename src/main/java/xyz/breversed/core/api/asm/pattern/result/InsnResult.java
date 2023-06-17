package xyz.breversed.core.api.asm.pattern.result;

import lombok.AllArgsConstructor;
import org.objectweb.asm.tree.AbstractInsnNode;

@AllArgsConstructor
public class InsnResult {

    public final AbstractInsnNode[] pattern;

    public AbstractInsnNode getFirst() {
        return pattern[0];
    }

    public AbstractInsnNode getLast() {
        return pattern[pattern.length - 1];
    }
}
