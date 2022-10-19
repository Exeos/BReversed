package xyz.breversed.api.detection;

import org.objectweb.asm.Opcodes;
import xyz.breversed.api.interfaces.IMethods;

public abstract class AbstractDetector implements IMethods, Opcodes {

    protected abstract boolean detect();
}
