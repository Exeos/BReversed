package xyz.breversed.transformers.generic;

import me.exeos.asmplus.utils.ASMUtils;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.MethodNode;
import xyz.breversed.api.asm.transformer.Transformer;

public class MethodAccessFixer extends Transformer {

    @Override
    protected void transform() {
        for (ClassNode classNode : getClasses()) {
            for (MethodNode methodNode : classNode.methods) {
                ASMUtils.removeAccessCodes(methodNode, Opcodes.ACC_BRIDGE, Opcodes.ACC_STRICT, Opcodes.ACC_SYNTHETIC);
            }
        }
    }
}
