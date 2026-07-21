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

import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

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
                List<BasicBlock> moveChain = buildMoveChain(jumpBlock.trueBranchBlock, new HashSet<>());

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

    /**
     * Builds a chain of following blocks that can safely be moved start
     * @param current Start block
     * @param visited Set to keep track of already visited Blocks, to avoid dups and inf loops
     * @return A list of blocks than can safely be moved to after start block
     */
    private List<BasicBlock> buildMoveChain(BasicBlock current, Set<BasicBlock> visited) {
        if (!visited.add(current) || current.predecessors.size() > 1) {
            return List.of();
        }

        List<BasicBlock> chain = new LinkedList<>();
        chain.add(current);

        switch (current) {
            case JumpBlock jumpBlock -> {
                jumpBlock.falseBranchBlock.ifPresent(fb -> chain.addAll(buildMoveChain(fb, visited)));
            }
            case FallTroughBlock ftb -> {
                List<BasicBlock> tail = buildMoveChain(ftb.fallTroughBlock, visited);
                if (tail.isEmpty()) {
                    return List.of();
                }
                chain.addAll(tail);
            }
            case SwitchBlock switchBlock -> {
                for (BasicBlock caseBlock : switchBlock.keyCaseMap.values()) {
                    chain.addAll(buildMoveChain(caseBlock, visited));
                }
                chain.addAll(buildMoveChain(switchBlock.defaultBlock, visited));
            }
            default -> {}
        }

        return chain;
    }
}
