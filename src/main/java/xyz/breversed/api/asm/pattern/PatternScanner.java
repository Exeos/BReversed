package xyz.breversed.api.asm.pattern;

import org.objectweb.asm.tree.AbstractInsnNode;
import org.objectweb.asm.tree.MethodNode;
import xyz.breversed.api.asm.utils.ASMUtil;
import xyz.breversed.api.asm.JarInterface;

public class PatternScanner implements JarInterface {

    private int[] pattern = null;

    /**
     *
     * @param pattern   Pattern of Opcodes to look after, use -1 for: labels, lines (& frames which get skipped by default) and use -2 for unknown
     */
    public PatternScanner(int[] pattern) {
        this.pattern = pattern;
    }

    public PatternScanner() {}

    /**
     * @param methodNode        Method to be scanned
     * @return                  The FIRST pattern found
     */
    public AbstractInsnNode scanMethod(MethodNode methodNode) {
        if (pattern == null)
            return null;

        AbstractInsnNode retPattern = null;

        for (AbstractInsnNode first : methodNode.instructions) {
            AbstractInsnNode last = ASMUtil.getNext(first, pattern.length - 1);

            if (first.getOpcode() != pattern[0] || last == null || last.getOpcode() != pattern[pattern.length - 1])
                continue;

            boolean match = true;

            /* We start with index 1 because we already know index 0 matches
            *  We end with length - 2 because we already know length - 1 matches */
            for (int i = 1; i <= pattern.length - 2; i++) {
                /* if pattern ant i is -2 we continue the scan no matter the opcode*/
                if (pattern[i] != -2 && ASMUtil.getNext(first, i).getOpcode() != pattern[i]) {
                    match = false;
                    break;
                }
            }

            if (match) {
                retPattern = first;
                break;
            }
        }

        return retPattern;
    }

    public void setPattern(int[] pattern) {
        this.pattern = pattern;
    }
}