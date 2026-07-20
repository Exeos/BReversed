package me.exeos.breversed.transformer.impl.bytus;

import me.exeos.asmplus.analysis.pattern.PatternScanner;
import me.exeos.asmplus.utils.InsnUtil;
import me.exeos.breversed.transformer.AbstractTransformer;
import me.exeos.breversed.transformer.context.MethodContext;
import org.objectweb.asm.tree.*;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

public class BytusFlowUnflatten extends AbstractTransformer {

    private final static PatternScanner storePattern = new PatternScanner()
            .matchInt()
            .matchOpCode(ISTORE)
            .matchOpCode(GOTO);

    private final static PatternScanner dispEntry = new PatternScanner()
            .matchInt()
            .matchOpCode(ISTORE);

    @Override
    public void transform(MethodContext context) {
        MethodNode methodNode = context.methodNode();
        if (!methodNode.tryCatchBlocks.isEmpty()) {
            return;
        }

        Map<Integer, LabelNode> keyLabelMap = new HashMap<>();
        for (AbstractInsnNode insnNode : methodNode.instructions) {
            if (insnNode instanceof LookupSwitchInsnNode ls) {
                for (int i = 0; i < ls.keys.size(); i++) {
                    keyLabelMap.put(ls.keys.get(i), ls.labels.get(i));
                }
                break;
            }
        }

        if (keyLabelMap.isEmpty()) {
            return;
        }

        var matches = dispEntry.scan(methodNode.instructions);
        if (matches.isEmpty()) {
           return;
        }

        AtomicInteger entryKey = new AtomicInteger();
        InsnUtil.getIntValue(matches.getFirst()).ifPresent(entryKey::set);

        methodNode.instructions.insertBefore(methodNode.instructions.getFirst(), new JumpInsnNode(GOTO, keyLabelMap.get(entryKey.get())));

        var storeMatches = storePattern.scan(methodNode.instructions);
        if (storeMatches.isEmpty()) {
           return;
        }

        for (AbstractInsnNode match : storeMatches) {
            InsnUtil.getIntValue(match).ifPresent(integer -> {
                methodNode.instructions.insertBefore(match, new JumpInsnNode(GOTO, keyLabelMap.get(integer)));
                remove(methodNode.instructions, match, 3);
            });
        }
    }

    private static void remove(InsnList from, AbstractInsnNode start, int amount) {
        for (int i = 0; i < amount - 1; i++) {
            if (start.getNext() == null) {
                break;
            }

            from.remove(start.getNext());
        }
        from.remove(start);
    }
}
