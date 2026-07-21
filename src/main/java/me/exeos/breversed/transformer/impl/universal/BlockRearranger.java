package me.exeos.breversed.transformer.impl.universal;

import me.exeos.asmplus.analysis.flow.FlowAnalyzer;
import me.exeos.asmplus.analysis.flow.block.BasicBlock;
import me.exeos.asmplus.analysis.flow.block.impl.FallTroughBlock;
import me.exeos.asmplus.analysis.flow.block.impl.JumpBlock;
import me.exeos.asmplus.analysis.flow.block.impl.SwitchBlock;
import me.exeos.breversed.transformer.AbstractTransformer;
import me.exeos.breversed.transformer.context.MethodContext;
import me.exeos.breversed.util.ListUtil;
import org.objectweb.asm.tree.InsnList;

import java.util.LinkedList;
import java.util.List;

public class BlockRearranger extends AbstractTransformer {

    @Override
    public void transform(MethodContext context) {
        List<BasicBlock> basicBlocks = FlowAnalyzer.getBasicBlocks(context.methodNode(), true, true);
        if(basicBlocks.isEmpty()) {
            return;
        }

        List<BasicBlock> rearranged = new LinkedList<>(basicBlocks);

        for (int i = 0; i < rearranged.size(); i++) {
            BasicBlock block = rearranged.get(i);
            if (block instanceof JumpBlock jumpBlock && jumpBlock.falseBranchBlock.isEmpty()) {
                List<BasicBlock> moveChain = new LinkedList<>();
                buildMoveChain(jumpBlock.trueBranchBlock, moveChain);

                for (int i1 = moveChain.size() - 1; i1 >= 0; i1--) {
                    ListUtil.move(rearranged, rearranged.indexOf(moveChain.get(i1)), rearranged.indexOf(block) + 1);
                }
            }
        }

        InsnList rebuild = new InsnList();
        for (BasicBlock block : rearranged) {
            rebuild.add(block.insnList());
        }

        context.methodNode().instructions = rebuild;
    }

    private void buildMoveChain(BasicBlock current, List<BasicBlock> moveChain) {
        // true branch block:
        // jump: adden & falsebranch, ende wenn kein falseBranch
        // falltough: adden & ft
        // switch: add & ende
        // terminal add & ende
        moveChain.add(current);
        switch (current) {
            case JumpBlock jumpBlock -> {
                var falseBlock = jumpBlock.falseBranchBlock;
                falseBlock.ifPresent(block -> buildMoveChain(block, moveChain));
            }
            case FallTroughBlock tfb -> {
                buildMoveChain(tfb.fallTroughBlock, moveChain);
            }
            default -> {}
        }
    }
}
