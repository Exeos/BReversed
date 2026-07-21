package me.exeos.breversed.transformer.impl.bytus;

import me.exeos.asmplus.analysis.flow.FlowAnalyzer;
import me.exeos.asmplus.analysis.flow.block.BasicBlock;
import me.exeos.asmplus.analysis.flow.block.impl.JumpBlock;
import me.exeos.asmplus.analysis.flow.block.impl.SwitchBlock;
import me.exeos.asmplus.analysis.pattern.PatternScanner;
import me.exeos.asmplus.utils.InsnUtil;
import me.exeos.breversed.transformer.AbstractTransformer;
import me.exeos.breversed.transformer.context.MethodContext;
import org.objectweb.asm.tree.InsnList;
import org.objectweb.asm.tree.JumpInsnNode;
import org.objectweb.asm.tree.LabelNode;
import org.objectweb.asm.tree.LookupSwitchInsnNode;

import java.util.*;
import java.util.concurrent.atomic.AtomicReference;
import java.util.stream.Collectors;

public class BytusFlowUnflatten extends AbstractTransformer {

    private final static PatternScanner storePattern = new PatternScanner()
            .matchInt()
            .matchOpCode(ISTORE);

    @Override
    public void transform(MethodContext context) {
        if (!context.methodNode().tryCatchBlocks.isEmpty()) {
            return;
        }

        List<BasicBlock> basicBlocks = FlowAnalyzer.getBasicBlocks(context.methodNode(), true);
        if (basicBlocks.isEmpty()) {
            return;
        }

        AtomicReference<Integer> entryKey = new AtomicReference<>(null);
        Map<Integer, LabelNode> keyLabelMap = new HashMap<>();

        var dispatcherBlock = findDispatcher(basicBlocks, entryKey, keyLabelMap);
        if (dispatcherBlock.isEmpty() || entryKey.get() == null) {
            return;
        }

        InsnList rebuild = new InsnList();
        rebuild.add(new JumpInsnNode(GOTO, keyLabelMap.get(entryKey.get())));
        for (BasicBlock basicBlock : basicBlocks) {
            rebuild.add(rebuildBlock(basicBlock, dispatcherBlock.get(), keyLabelMap));
        }

        context.methodNode().instructions = rebuild;
    }

    private InsnList rebuildBlock(BasicBlock block, BasicBlock dispatcherBlock, Map<Integer, LabelNode> keyLabelMap) {
        InsnList rebuild = new InsnList();

        if (block instanceof JumpBlock jumpBlock) {
            InsnList jumpBlockInsn = jumpBlock.insnList();
            if (jumpBlock.falseBranchBlock.isEmpty() && jumpBlock.trueBranchBlock.equals(dispatcherBlock)) {
                InsnUtil.getIntValue(jumpBlock.dispatcher.getPrevious().getPrevious()).ifPresent(key -> {
                    jumpBlockInsn.insert(jumpBlock.dispatcher, new JumpInsnNode(GOTO, keyLabelMap.get(key)));

                    jumpBlockInsn.remove(jumpBlock.dispatcher.getPrevious());
                    jumpBlockInsn.remove(jumpBlock.dispatcher.getPrevious());
                    jumpBlockInsn.remove(jumpBlock.dispatcher);
                });
            }

            rebuild.add(jumpBlockInsn);
        } else {
            rebuild.add(block.insnList());
        }

        return rebuild;
    }
    
    private Optional<SwitchBlock> findDispatcher(List<BasicBlock> blocks, AtomicReference<Integer> entryKey, Map<Integer, LabelNode> keyMap) {
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

            if (block instanceof SwitchBlock switchBlock && switchBlock.dispatcher instanceof LookupSwitchInsnNode ls) {
                for (int i = 0; i < ls.keys.size(); i++) {
                    keyMap.put(ls.keys.get(i), ls.labels.get(i));
                }

                return Optional.of(switchBlock);
            }

            blockDeque.addAll(block.normalSuccessors.stream().filter(successor -> !seen.contains(successor)).collect(Collectors.toSet()));
        }

        return Optional.empty();
    }
}
