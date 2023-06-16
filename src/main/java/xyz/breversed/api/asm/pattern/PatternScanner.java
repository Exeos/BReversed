package xyz.breversed.api.asm.pattern;

import org.objectweb.asm.Opcodes;
import org.objectweb.asm.tree.AbstractInsnNode;
import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.MethodNode;
import xyz.breversed.api.asm.pattern.result.ClassResult;
import xyz.breversed.api.asm.pattern.result.InsnResult;
import xyz.breversed.api.asm.pattern.result.MethodResult;
import xyz.breversed.api.asm.utils.ASMUtil;
import xyz.breversed.api.asm.JarInterface;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class

PatternScanner implements JarInterface, PatternParts, Opcodes {

    /*
     * Pattern of opcodes to be looked after
     */
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
     * @param archive           Classes to be scanned
     * @return                  Classes the pattern was found in with array of methods and patterns in those.
     *                          Check out the "result" package for more context.
     */
    public List<ClassResult> scanArchive(List<ClassNode> archive) {
        List<ClassResult> results = new ArrayList<>();

        for (ClassNode classNode : archive) {
            ClassResult result = scanClass(classNode);

            if (result != null)
                results.add(result);
        }

        return results;
    }

    /**
     * @param classNode         Class to be scanned
     * @return                  Class the pattern was found in, array of methods and patterns in those.
     *                          Check out the "result" package for more context.
     */
    public ClassResult scanClass(ClassNode classNode) {
        List<MethodResult> results = new ArrayList<>();

        for (MethodNode methodNode : classNode.methods) {
            List<InsnResult> foundPatterns = scanMethod(methodNode);

            if (!foundPatterns.isEmpty())
                results.add(new MethodResult(methodNode, foundPatterns));
        }

        return results.isEmpty() ? null : new ClassResult(classNode, results);
    }

    /**
     * @param methodNode        Method to be scanned
     * @return                  The patterns found
     */
    public List<InsnResult> scanMethod(MethodNode methodNode) {
        if (pattern == null)
            return null;

        List<InsnResult> foundPatterns = new ArrayList<>();

        for (AbstractInsnNode first : methodNode.instructions) {
            AbstractInsnNode last = ASMUtil.getNext(first, pattern.length - 1);

            if (last == null)
                break;

            /* Speeding up the process */
            if (!containsSKIP() && (!match(first, pattern[0]) || !match(last, pattern[pattern.length - 1])))
                continue;

            boolean match = true;
            Integer toSkip = null;

            List<AbstractInsnNode> foundPattern = new ArrayList<>();

            int i = 0;
            int patternIndex = 0;
            while (patternIndex <= pattern.length - 1) {
                AbstractInsnNode next = ASMUtil.getNext(first, i);
                if (next == null) {
                    match = false;
                    break;
                }
                /* Handling P_SKIPTO */
                if (toSkip == null && pattern[patternIndex] == P_SKIPTO) {
                    toSkip = pattern[patternIndex + 1];
                    continue;
                }
                /* increasing i here because if pattern[patternIndex] == P_SKIPTO "next" should also get added */
                i++;

                if (toSkip != null) {
                    if (match(next, toSkip)) {
                        patternIndex++;
                        toSkip = null;
                    } else {
                        foundPattern.add(next);
                        continue;
                    }
                }

                if (!match(next, pattern[patternIndex])) {
                    match = false;
                    break;
                } else {
                    foundPattern.add(next);
                }
                patternIndex++;
            }

            if (match) {
                foundPatterns.add(new InsnResult(foundPattern.toArray(new AbstractInsnNode[0])));
            }
        }

        return foundPatterns;
    }

    public void setPattern(int[] pattern) {
        this.pattern = pattern;
    }

    private boolean containsSKIP() {
        return Arrays.asList(Arrays.stream(pattern).boxed().toArray(Integer[]::new)).contains(P_SKIPTO);
    }

    private boolean match(AbstractInsnNode toCheck, int part) {
        return part == P_ANY || (part == P_NUMBER && ASMUtil.isLongOrIntPush(toCheck)) || toCheck.getOpcode() == part || toCheck.getType() == part - 300;
    }
}