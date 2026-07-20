package me.exeos.breversed.transformer.impl.bytus;

import me.exeos.asmplus.analysis.flow.FlowAnalyzer;
import me.exeos.asmplus.analysis.flow.block.BasicBlock;
import me.exeos.asmplus.analysis.flow.block.impl.SwitchBlock;
import me.exeos.asmplus.analysis.pattern.PatternScanner;
import me.exeos.asmplus.utils.InsnUtil;
import me.exeos.breversed.transformer.AbstractTransformer;
import me.exeos.breversed.transformer.context.MethodContext;

import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;
import java.util.stream.Collectors;

public class BytusFlow extends AbstractTransformer {

    private final static PatternScanner storePattern = new PatternScanner()
            .matchInt()
            .matchOpCode(ISTORE);

    @Override
    public void transform(MethodContext context) {
        if (!context.methodNode().tryCatchBlocks.isEmpty()) {
            return;
        }

        List<BasicBlock> basicBlocks = FlowAnalyzer.getBasicBlocks(context.methodNode());
        if (basicBlocks.isEmpty()) {
            return;
        }

        AtomicReference<Integer> entryKey = new AtomicReference<>(null);
        var dispatcherBlock = findDispatcher(basicBlocks, entryKey);
        if (dispatcherBlock.isEmpty() || entryKey.get() == null) {
            return;
        }

        // wip lol
    }
    
    private Optional<SwitchBlock> findDispatcher(List<BasicBlock> blocks, AtomicReference<Integer> entryKey) {
        Set<BasicBlock> seen = new HashSet<>();
        Deque<BasicBlock> blockDeque = new ArrayDeque<>();
        blockDeque.add(blocks.getFirst());

        while (!blockDeque.isEmpty()) {
            BasicBlock block = blockDeque.pop();
            seen.add(block);


            var matches = storePattern.scan(block.insnList());
            if (!matches.isEmpty()) {
                InsnUtil.getIntValue(matches.getFirst()).ifPresent(entryKey::set);
            }

            if (block instanceof SwitchBlock switchBlock) {
                return Optional.of(switchBlock);
            }

            blockDeque.addAll(block.normalSuccessors.stream().filter(successor -> !seen.contains(successor)).collect(Collectors.toSet()));
        }

        return Optional.empty();
    }
}
