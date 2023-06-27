package xyz.breversed.core.detectors.scuti;

import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.MethodNode;
import xyz.breversed.core.api.asm.detection.AbstractDetector;

public class ScutiInvokeDynamic extends AbstractDetector {

    @Override
    protected boolean detect() {
        boolean detected = false;

        for (ClassNode classNode : getClasses()) {
            MethodNode stringDecryptMethod = classNode.methods.stream().filter(methodNode -> methodNode.name.startsWith("     ") && methodNode.access == 4170 &&
                    methodNode.desc.equals("(Ljava/lang/String;)Ljava/lang/String;")).findFirst().orElse(null);
            MethodNode callDecryptMethod = classNode.methods.stream().filter(methodNode -> methodNode.access == 4169 &&
                    methodNode.desc.equals("(Ljava/lang/invoke/MethodHandles$Lookup;Ljava/lang/String;Ljava/lang/invoke/MethodType;Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;Ljava/lang/Integer;)Ljava/lang/invoke/CallSite;")).findFirst().orElse(null);

            if (stringDecryptMethod != null) {
                addContext("String decryptor method found for InvokeDynamic");
                detected = true;
            }
            if (callDecryptMethod != null) {
                addContext("Call decryptor method found for InvokeDynamic");
                detected = true;
            }
        }

        return detected;
    }
}