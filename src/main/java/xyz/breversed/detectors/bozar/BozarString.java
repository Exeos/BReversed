package xyz.breversed.detectors.bozar;

import xyz.breversed.api.asm.detection.AbstractDetector;
import xyz.breversed.api.asm.pattern.PatternParts;

public class BozarString extends AbstractDetector implements PatternParts {

    @Override
    protected boolean detect() {
        /* Always come together */
        return new BozarNumber().detect();
    }
}
