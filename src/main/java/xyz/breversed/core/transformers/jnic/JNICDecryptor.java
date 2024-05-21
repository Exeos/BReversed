package xyz.breversed.core.transformers.jnic;

import me.exeos.asmplus.pattern.PatternScanner;
import me.exeos.asmplus.pattern.result.InsnResult;
import me.exeos.asmplus.utils.ASMUtils;
import me.exeos.jlib.data.Pair;
import me.exeos.jlib.reflection.ClassDefiner;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.tree.*;
import xyz.breversed.core.api.asm.transformer.Transformer;
import xyz.breversed.core.detectors.jnic.JNICLoader;

import java.util.ArrayList;
import java.util.List;

public class JNICDecryptor extends Transformer {

    @Override
    public void transform() {
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

                List<InsnResult> tempFilePatter = cTempFilePatter(methodNode);
                List<InsnResult> loadLibPatter = loadLibPatter(methodNode);

                if (tempFilePatter.size() != 1 || loadLibPatter.size() != 1) {
                    System.out.println("Patterns found > or < than 1. TFP: " + tempFilePatter.size() + " LFP:" + loadLibPatter.size());
                    break;
                }

                InsnResult tempFileResult = tempFilePatter.get(0);
                InsnResult loadLibResult = loadLibPatter.get(0);

                /* insert new instructions */
                methodNode.instructions.insertBefore(
                        tempFileResult.getFirst(),
                        ASMUtils.convertToIList(
                                patchedFileCreation(((VarInsnNode) tempFileResult.pattern[4]).var /* ALOAD X */)
                        )
                );

                /* remove old instructions */
                ASMUtils.removeInstructions(tempFileResult.pattern, methodNode);
                for (int i = 0; i < loadLibResult.pattern.length - 1; i++) {
                    methodNode.instructions.remove(loadLibResult.pattern[i]);
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

    private List<InsnResult> cTempFilePatter(MethodNode methodNode) {
        return new PatternScanner(new int[] {
                LDC,
                ACONST_NULL,
                INVOKESTATIC,
                ASTORE,
                ALOAD,
                INVOKEVIRTUAL
        }).scanMethod(methodNode);
    }

    private List<AbstractInsnNode> patchedFileCreation(int varIndex) {
        ArrayList<AbstractInsnNode> insns = new ArrayList<>();

        /* File file = new File("lib") */
        insns.add(new TypeInsnNode(NEW, "java/io/File"));
        insns.add(new InsnNode(DUP));
        insns.add(new LdcInsnNode("io/decrypted.dll"));
        insns.add(new MethodInsnNode(INVOKESPECIAL, "java/io/File", "<init>", "(Ljava/lang/String;)V"));
        insns.add(new VarInsnNode(ASTORE, varIndex));

        /* file.creatNewFile(); */
        insns.add(new VarInsnNode(ALOAD, varIndex));
        insns.add(new MethodInsnNode(INVOKEVIRTUAL, "java/io/File", "createNewFile", "()Z"));
        insns.add(new InsnNode(POP));

        return insns;
    }

    private List<InsnResult> loadLibPatter(MethodNode methodNode) {
        return new PatternScanner(new int[] {
                ALOAD,
                INVOKEVIRTUAL,
                INVOKESTATIC,
                RETURN
        }).scanMethod(methodNode);
    }
}
