package xyz.breversed.transformers;

import xyz.breversed.api.asm.pattern.PatternParts;
import xyz.breversed.api.asm.pattern.PatternScanner;
import xyz.breversed.api.asm.pattern.result.ClassResult;
import xyz.breversed.api.asm.transformer.Transformer;

import java.util.List;

public class TestTransformer extends Transformer implements PatternParts {

    @Override
    protected void transform() {
        int[] pattern = new int[] {
                P_LABEL,
                P_LINE,
                NEW,
                DUP,
                LDC,
                P_ANY,
                ASTORE
        };
        PatternScanner patternScanner = new PatternScanner(pattern);

        List<ClassResult> results = patternScanner.scanArchive(getClasses());
        int index = 1;
        for (ClassResult result : results) {
            System.out.println("________" + index + "________");
            
            System.out.println("_________________");
            index++;
        }
    }
}