package xyz.breversed.transformers.bozar;

import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.FieldNode;
import org.objectweb.asm.tree.MethodNode;
import xyz.breversed.api.asm.pattern.PatternParts;
import xyz.breversed.api.asm.pattern.PatternScanner;
import xyz.breversed.api.asm.pattern.result.InsnResult;
import xyz.breversed.api.asm.transformer.Transformer;
import xyz.breversed.api.asm.utils.ASMUtil;

import java.util.List;

public class BozarHeavyFlow extends Transformer implements PatternParts {

    @Override
    protected void transform() {
        PatternScanner patternScanner = new PatternScanner();

        for (ClassNode classNode : getClasses()) {
            /* Bozar always adds this field */
            FieldNode flowField = ASMUtil.getField(classNode, String.valueOf((char)5096), "J");
            if (flowField == null)
                continue;

            classNode.fields.remove(flowField);

        }
    }

    private List<InsnResult> before0switchB(PatternScanner patternScanner, MethodNode methodNode) {
        patternScanner.setPattern(new int[] {
                LABEL,
                FIELD_INSN,
                LABEL,
                P_ANY,
                GOTO,
                LABEL,
                P_ANY,
                P_ANY,
                LABEL,
                //switch
                DUP2,
                L2I,
                LOOKUPSWITCH
        });
        return patternScanner.scanMethod(methodNode);
    }

}
