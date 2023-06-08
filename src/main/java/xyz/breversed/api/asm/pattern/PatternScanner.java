package xyz.breversed.api.asm.pattern;

import org.objectweb.asm.tree.AbstractInsnNode;
import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.MethodNode;
import xyz.breversed.api.asm.pattern.result.ClassResult;
import xyz.breversed.api.asm.pattern.result.MethodResult;
import xyz.breversed.api.asm.utils.ASMUtil;
import xyz.breversed.api.asm.JarInterface;

import java.util.ArrayList;
import java.util.List;

public class PatternScanner implements JarInterface {

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
            List<AbstractInsnNode> foundPatterns = scanMethod(methodNode);

            if (!foundPatterns.isEmpty())
                results.add(new MethodResult(methodNode, foundPatterns));
        }

        return results.isEmpty() ? null : new ClassResult(classNode, results);
    }

    /**
     * @param methodNode        Method to be scanned
     * @return                  The patterns found
     */
    public List<AbstractInsnNode> scanMethod(MethodNode methodNode) {
        if (pattern == null)
            return null;

        List<AbstractInsnNode> foundPatterns = new ArrayList<>();

        for (AbstractInsnNode first : methodNode.instructions) {
            AbstractInsnNode last = ASMUtil.getNext(first, pattern.length - 1);

            if (last == null)
                break;

            if (first.getOpcode() != pattern[0] || last.getOpcode() != pattern[pattern.length - 1])
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

            if (match)
                foundPatterns.add(first);
        }

        return foundPatterns;
    }

    public void setPattern(int[] pattern) {
        this.pattern = pattern;
    }
}