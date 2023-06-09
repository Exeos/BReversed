package xyz.breversed.api.asm;

import lombok.experimental.UtilityClass;
import org.objectweb.asm.ClassReader;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.tree.ClassNode;
import xyz.breversed.BReversed;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;
import java.util.jar.JarOutputStream;

@UtilityClass
public class JarLoader {

    public final ArrayList<ClassNode> classes = new ArrayList<>();
    public final HashMap<String, byte[]> files = new HashMap<>();

    public void load() throws IOException {
        JarFile jarIn = new JarFile(BReversed.INSTANCE.config.getPath() + BReversed.INSTANCE.config.jars[0]);
        Enumeration<? extends JarEntry> entries = jarIn.entries();
        JarEntry entry = entries.nextElement();

        while (entries.hasMoreElements()) {
            InputStream stream = jarIn.getInputStream(entry);
            byte[] entryBytes = stream.readAllBytes();

            if (entry.getRealName().endsWith(".class") && isClass(entryBytes)) {
                ClassReader classReader = new ClassReader(entryBytes);
                ClassNode classNode = new ClassNode();

                classReader.accept(classNode, ClassReader.SKIP_FRAMES);
                classes.add(classNode);
            } else {
                files.put(entry.getRealName(), entryBytes);
            }

            entry = entries.nextElement();
        }
    }

    public void export() throws IOException {
        JarOutputStream jarOut = new JarOutputStream(new FileOutputStream(BReversed.INSTANCE.config.getPath() + BReversed.INSTANCE.config.jars[1]));

        for (ClassNode classNode : classes) {
            ClassWriter classWriter = new ClassWriter(ClassWriter.COMPUTE_MAXS);
            classNode.accept(classWriter);

            writeEntry(jarOut, classNode.name, classWriter.toByteArray());
        }

        for (Map.Entry<String, byte[]> e : files.entrySet()) {
            writeEntry(jarOut, e.getKey(), e.getValue());
        }

        jarOut.finish();
    }

    private void writeEntry(JarOutputStream outputStream, String name, byte[] bytes) throws IOException {
        JarEntry entry = new JarEntry(name);
        entry.setSize(bytes.length);

        outputStream.putNextEntry(entry);
        outputStream.write(bytes);
        outputStream.closeEntry();
    }

    private boolean isClass(byte[] file) {
        return new BigInteger(1, new byte[] { file[0], file[1], file[2], file[3] }).intValue() == -889275714;
    }
}