package xyz.breversed.transformers.jnic;

import me.exeos.jlib.data.Pair;
import me.exeos.jlib.reflection.ClassDefiner;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.tree.*;
import xyz.breversed.api.asm.transformer.Transformer;
import xyz.breversed.detectors.jnic.JNICLoader;

import java.util.Scanner;

public class JNICInvoker extends Transformer {

    @Override
    public void transform() {
        System.out.println("WARNING! THIS TRANSFORMER IS EXPLOITABLE AS IT USES REFLECTIONS, MAKE SURE ALL CLASSES IN dev/jnic/** ARE SAFE TO RUN");
        System.out.println("Continue? yes/no");
        String in = new Scanner(System.in).nextLine();
        if (!in.equals("yes")) {
            return;
        }

        int foundClassCount = 0;
        for (ClassNode classNode : getClasses()) {
            /* skip if class name doesn't match */
            if (!JNICLoader.isJNICLoader(classNode.name)) {
                continue;
            }

            for (MethodNode methodNode : classNode.methods) {
                /* skip if method not class initializer */
                if (!methodNode.name.equals("<clinit>") || !methodNode.desc.equals("()V")) {
                    continue;
                }

                try {
                    invoke(classNode);
                } catch (Exception e) {
                    System.out.println("Failed to invoke patched JNIC class.");
                    break;
                }
            }
            foundClassCount++;
        }

        /* give information of how the transformation went */
        if (foundClassCount != 1) {
            System.out.println("WARNING: found > or < than 1 JNICLoader class. Amount found: " + foundClassCount);
        }
    }

    private void invoke(ClassNode classNode) throws Exception {
        Pair<String, byte[]> encBin = getEncBin();
        if (encBin == null) {
            System.out.println("Failed to invoke, couldn't find enc bin");
            return;
        }
        /* init */
        ClassDefiner classDefiner = new ClassDefiner(null);
        classDefiner.resources.put(encBin.key, encBin.value);

        /* add class and resource */
        String[] split = classNode.name.split("/");
        String preFix = classNode.name.substring(0, classNode.name.length() - split[split.length - 1].length());
        for (ClassNode node : getClasses()) {
            if (!node.name.startsWith(preFix)) {
                continue;
            }

            ClassWriter classWriter = new ClassWriter(0);
            node.accept(classWriter);

            classDefiner.classes.put(node.name.replace("/", "."), classWriter.toByteArray());
        }

        Class.forName(classNode.name.replace("/", "."), true, classDefiner);
    }

    private Pair<String, byte[]> getEncBin() {
        for (String name : getResources().keySet()) {
            if (name.startsWith("dev/jnic/") && name.endsWith(".dat")) {
                return new Pair<>(name, getResources().get(name));
            }
        }

        return null;
    }
}
