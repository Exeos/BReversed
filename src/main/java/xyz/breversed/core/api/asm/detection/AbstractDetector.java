package xyz.breversed.core.api.asm.detection;

import org.objectweb.asm.Opcodes;
import xyz.breversed.core.api.asm.JarInterface;

import java.util.ArrayList;

public abstract class AbstractDetector implements JarInterface, Opcodes {

    public final ArrayList<String> context = new ArrayList<>();

    protected abstract boolean detect();

    protected void addContext(String context) {
        if (!this.context.contains(context))
            this.context.add(context);
    }

    @Override
    public String toString() {
        return getClass().getSimpleName();
    }
}