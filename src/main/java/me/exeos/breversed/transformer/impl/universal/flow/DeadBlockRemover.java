package me.exeos.breversed.transformer.impl.universal.flow;

import me.exeos.asmplus.analysis.flow.FlowAnalyzer;
import me.exeos.asmplus.analysis.flow.block.BasicBlock;
import me.exeos.breversed.transformer.AbstractTransformer;
import me.exeos.breversed.transformer.context.MethodContext;
import org.objectweb.asm.tree.InsnList;
import org.objectweb.asm.tree.MethodNode;

import java.util.*;
import java.util.stream.Collectors;

public class DeadBlockRemover extends AbstractTransformer {

    private final boolean skipMethodsWithTcp;

    public DeadBlockRemover() {
        this(false);
    }

    public DeadBlockRemover(boolean skipMethodsWithTcp) {
        this.skipMethodsWithTcp = skipMethodsWithTcp;
    }

    @Override
    public void transform(MethodContext context) {
        MethodNode methodNode = context.methodNode();
        if (skipMethodsWithTcp && !methodNode.tryCatchBlocks.isEmpty()) {
            return;
        }

        List<BasicBlock> blocks = FlowAnalyzer.getBasicBlocks(methodNode, true, true);
        if (blocks.isEmpty()) {
            return;
        }

        Set<BasicBlock> valid = getUsedBlocks(blocks.getFirst());
        InsnList rebuild = new InsnList();
        for (BasicBlock block : blocks) {
            if (valid.contains(block)) {
                rebuild.add(block.insnList());
            }
        }

        methodNode.instructions = rebuild;
    }

    private Set<BasicBlock> getUsedBlocks(BasicBlock start) {
        Deque<BasicBlock> blockDeque = new ArrayDeque<>();
        Set<BasicBlock> seen = new HashSet<>();
        blockDeque.add(start);

        while (!blockDeque.isEmpty()) {
            BasicBlock block = blockDeque.removeFirst();
            seen.add(block);

            blockDeque.addAll(block.successors.stream().filter(successor -> !seen.contains(successor)).collect(Collectors.toSet()));
        }

        return seen;
    }
}
