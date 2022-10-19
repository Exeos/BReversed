package xyz.breversed.api.transformer;

import org.objectweb.asm.Opcodes;
import xyz.breversed.api.interfaces.IMethods;

public abstract class Transformer implements IMethods, Opcodes {

    protected abstract void transform();

    @Override
    public String toString() {
        return getClass().getSimpleName() + " transformer";
    }
}
