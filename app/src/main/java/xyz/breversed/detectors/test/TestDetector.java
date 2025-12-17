package xyz.breversed.detectors.test;

import me.exeos.asmplus.pscan.PatternScanner;
import org.objectweb.asm.tree.*;
import xyz.breversed.api.asm.detection.AbstractDetector;

public class TestDetector extends AbstractDetector {

    @Override
    protected boolean detect() {
        PatternScanner testPattern = new PatternScanner()
                .matchOpCode(ALOAD)
                .multiMatch()
                .matchOpCode(INVOKESPECIAL)
                .matchCustom(insn -> {
                    if (insn instanceof MethodInsnNode methodInsnNode) {
                        return methodInsnNode.owner.equals("java/io/InputStream");
                    }
                    return false;
                })
                .endMultiMatch()
                .matchOpCode(ALOAD)
                .matchOpCode(ICONST_0)
                .any(2);

        for (ClassNode classNode : getClasses()) {
            for (MethodNode methodNode : classNode.methods) {
                for (AbstractInsnNode insnNode : testPattern.scan(methodNode)) {
                    System.out.println(classNode.name + "." + methodNode.name + ": " + methodNode.instructions.indexOf(insnNode));
                }
            }
        }
        return false;
    }
}
