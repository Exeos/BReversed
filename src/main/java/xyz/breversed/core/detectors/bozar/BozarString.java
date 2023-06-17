package xyz.breversed.core.detectors.bozar;

import xyz.breversed.core.api.asm.detection.AbstractDetector;
import xyz.breversed.core.api.asm.pattern.PatternParts;

public class BozarString extends AbstractDetector implements PatternParts {

    @Override
    protected boolean detect() {
        /* Always come together */
        return new BozarNumber().detect();
    }
}
