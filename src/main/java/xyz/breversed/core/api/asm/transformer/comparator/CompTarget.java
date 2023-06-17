package xyz.breversed.core.api.asm.transformer.comparator;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
public @interface CompTarget {

    /* Package to apply the comparator to */
    String target();

}
