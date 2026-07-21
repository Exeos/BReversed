package me.exeos.breversed.transformer.impl.universal;

import me.exeos.asmplus.analysis.flow.FlowAnalyzer;
import me.exeos.asmplus.analysis.flow.block.BasicBlock;
import me.exeos.asmplus.analysis.flow.block.impl.FallTroughBlock;
import me.exeos.asmplus.analysis.flow.block.impl.JumpBlock;
import me.exeos.breversed.transformer.AbstractTransformer;
import me.exeos.breversed.transformer.context.MethodContext;
import me.exeos.breversed.util.ListUtil;
import org.objectweb.asm.tree.InsnList;
import org.objectweb.asm.tree.MethodNode;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

public class UselessJumpRemover extends AbstractTransformer {

    @Override
    public void transform(MethodContext context) {
        List<BasicBlock> basicBlocks = FlowAnalyzer.getBasicBlocks(context.methodNode(), false, true);
        if(basicBlocks.isEmpty()) {
            return;
        }

        for (BasicBlock block : basicBlocks) {
            if (block instanceof JumpBlock jumpBlock) {
                int nextIndex = basicBlocks.indexOf(block) + 1;
                if (basicBlocks.size() <= nextIndex) {
                    continue;
                }

                BasicBlock next = basicBlocks.get(nextIndex);
                if (jumpBlock.trueBranchBlock.equals(next)) {
                    context.methodNode().instructions.remove(jumpBlock.dispatcher);
                }
            }
        }
    }
}
