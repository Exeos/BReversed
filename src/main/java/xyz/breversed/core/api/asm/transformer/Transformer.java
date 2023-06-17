package xyz.breversed.core.api.asm.transformer;

import org.objectweb.asm.Opcodes;
import xyz.breversed.core.api.asm.JarInterface;

public abstract class Transformer implements JarInterface, Opcodes {

    protected abstract void transform();

    @Override
    public String toString() {
        return getClass().getSimpleName() + " transformer";
    }
}
