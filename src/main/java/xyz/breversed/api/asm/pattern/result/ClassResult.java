package xyz.breversed.api.asm.pattern.result;

import lombok.AllArgsConstructor;
import org.objectweb.asm.tree.ClassNode;

import java.util.List;

@AllArgsConstructor
public class ClassResult {

    public final ClassNode classNode;
    public final List<MethodResult> methodResults;

}
