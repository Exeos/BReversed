package xyz.breversed.api.transformer;

import xyz.breversed.api.interfaces.IMethods;

public abstract class Transformer implements IMethods {

    protected abstract void transform();

    @Override
    public String toString() {
        return getClass().getSimpleName() + " transformer";
    }
}
