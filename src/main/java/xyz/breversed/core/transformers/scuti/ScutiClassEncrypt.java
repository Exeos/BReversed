package xyz.breversed.core.transformers.scuti;

import me.exeos.asmplus.pattern.PatternParts;
import me.exeos.asmplus.pattern.PatternScanner;
import me.exeos.asmplus.pattern.result.InsnResult;
import me.exeos.asmplus.utils.ASMUtils;
import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.MethodNode;
import xyz.breversed.core.api.asm.transformer.Transformer;

import java.util.ArrayList;
import java.util.List;

// TODO: Improve code quality
public class ScutiClassEncrypt extends Transformer implements PatternParts {

    @Override
    protected void transform() {
        int[] keys = new int[] { -1, -1 }; // first value is the string key, the second is the file key
        for (ClassNode classNode : getClasses()) {
            keys[0] = getStringDecryptionKey(classNode);
            keys[1] = getFileDecryptionKey(classNode);
            if (keys[0] != -1)
                break;
        }
        if (keys[0] == -1 && keys[1] == -1) // if we didn't find the keys, return
            return;

        List<String> toRemove = new ArrayList<>();
        for (String key : new ArrayList<>(getResources().keySet())) { // iterate through all the files, decrypt them, add the decrypted file
            String decryptedName = decryptString(key, keys[0]);
            if (decryptedName.contains(".")) continue;

            byte[] decryptedFile = decryptFile(getResources().get(key), keys[1]);
            getResources().put(decryptedName + ".class", decryptedFile);
            toRemove.add(key);
        }
        for (String key : toRemove) // remove the encrypted files
            getResources().remove(key);
    }

    private String decryptString(String string, int key) {
        StringBuilder decrypted = new StringBuilder();
        for (int i = 0; i < string.length(); i++) {
            decrypted.append((char)(string.charAt(i) ^ key));
        }
        return decrypted.toString();
    }
    private byte[] decryptFile(byte[] byArray, int key) {
        byte[] byArray2 = new byte[byArray.length];
        int n = 0;
        while (n < byArray.length) {
            byArray2[n] = (byte)(byArray[n] ^ key);
            ++n;
        }
        return byArray2;
    }

    private int getStringDecryptionKey(ClassNode classNode) {
        MethodNode decryptMethod = classNode.methods.stream()
                .filter(methodNode -> methodNode.desc.equals("(Ljava/lang/String;)Ljava/lang/String;"))
                .findFirst().orElse(null);
        if (decryptMethod == null)
            return -1;

        PatternScanner patternScanner = new PatternScanner(new int[] {
                P_NUMBER,
                IXOR
        });
        for (InsnResult result : patternScanner.scanMethod(decryptMethod))
            return ASMUtils.getIntValue(result.getFirst());

        return -1;
    }
    private int getFileDecryptionKey(ClassNode classNode) {
        MethodNode decryptMethod = classNode.methods.stream()
                .filter(methodNode -> methodNode.desc.equals("([B)[B"))
                .findFirst().orElse(null);
        if (decryptMethod == null)
            return -1;

        PatternScanner patternScanner = new PatternScanner(new int[] {
                P_NUMBER,
                IXOR
        });
        for (InsnResult result : patternScanner.scanMethod(decryptMethod))
            return ASMUtils.getIntValue(result.getFirst());

        return -1;
    }
}