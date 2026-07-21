package me.exeos.breversed.transformer.impl.universal;

import me.exeos.asmplus.analysis.flow.FlowAnalyzer;
import me.exeos.asmplus.analysis.flow.block.BasicBlock;
import me.exeos.asmplus.analysis.flow.block.impl.JumpBlock;
import me.exeos.breversed.transformer.AbstractTransformer;
import me.exeos.breversed.transformer.context.MethodContext;
import org.objectweb.asm.tree.AbstractInsnNode;
import org.objectweb.asm.tree.InsnList;

import java.util.List;

public class IndirectJumpRemover extends AbstractTransformer {

    @Override
    public void transform(MethodContext context) {
        List<BasicBlock> basicBlocks = FlowAnalyzer.getBasicBlocks(context.methodNode(), false, true);
        if(basicBlocks.isEmpty()) {
            return;
        }

        for (BasicBlock block : basicBlocks) {
            if (block instanceof JumpBlock jumpBlock) {
                if (jumpBlock.trueBranchBlock instanceof JumpBlock tjb && tjb.falseBranchBlock.isEmpty() && !hasSideEffects(tjb.insnList())) {
                    jumpBlock.dispatcher.label = tjb.dispatcher.label;
                }
            }
        }
    }

    private boolean hasSideEffects(InsnList insnList) {
        for (AbstractInsnNode insnNode : insnList) {
            if (insnNode.getType() != AbstractInsnNode.LABEL && insnNode.getOpcode() != GOTO) {
                return true;
            }
        }

        return false;
    }
}
