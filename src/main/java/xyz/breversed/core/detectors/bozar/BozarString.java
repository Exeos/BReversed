package xyz.breversed.core.detectors.bozar;

import xyz.breversed.core.api.asm.detection.AbstractDetector;

public class BozarString extends AbstractDetector {

    @Override
    protected boolean detect() {
        /* Always come together */
        return new BozarNumber().detect();
    }
}
