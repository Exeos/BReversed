package me.exeos.breversed.transformer.impl.universal;

import me.exeos.asmplus.analysis.flow.FlowAnalyzer;
import me.exeos.asmplus.analysis.flow.block.BasicBlock;
import me.exeos.breversed.transformer.AbstractTransformer;
import me.exeos.breversed.transformer.context.MethodContext;
import org.objectweb.asm.tree.InsnList;
import org.objectweb.asm.tree.MethodNode;

import java.util.*;

public class DeadBlockRemover extends AbstractTransformer {

    @Override
    public void transform(MethodContext context) {
        MethodNode methodNode = context.methodNode();
        if (!methodNode.tryCatchBlocks.isEmpty()) {
            return;
        }

        List<BasicBlock> blocks = FlowAnalyzer.getBasicBlocks(methodNode, true);
        if (blocks.isEmpty()) {
            return;
        }

        Deque<BasicBlock> blockDeque = new ArrayDeque<>();
        Set<BasicBlock> seen = new HashSet<>();
        blockDeque.add(blocks.getFirst());

        InsnList rebuild = new InsnList();
        while (!blockDeque.isEmpty()) {
            BasicBlock block = blockDeque.pop();
            seen.add(block);

            rebuild.add(block.insnList());
            for (BasicBlock successor : block.normalSuccessors) {
                if (!seen.contains(successor)) {
                    blockDeque.add(successor);
                }
            }
        }

        methodNode.instructions = rebuild;
    }
}
