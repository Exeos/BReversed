package xyz.breversed.api;

import lombok.experimental.UtilityClass;
import org.apache.commons.io.IOUtils;
import org.objectweb.asm.ClassReader;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.tree.ClassNode;
import xyz.breversed.BReversed;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;
import java.util.jar.JarOutputStream;
import java.util.stream.Stream;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

@UtilityClass
public class JarLoader {

    public final ArrayList<ClassNode> classes = new ArrayList<>();
    public final Map<String, byte[]> files = new HashMap<>();

    public void loadJar() {
        final File input = new File(BReversed.INSTANCE.config.path + BReversed.INSTANCE.config.jars[0]);

        try {
            loadFiles(input);
            loadClasses(input);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void exportJar() {
        final File output = new File(BReversed.INSTANCE.config.path + BReversed.INSTANCE.config.jars[1]);

        try {
            final JarOutputStream jar = new JarOutputStream(new FileOutputStream(output));
            jar.setMethod(ZipEntry.DEFLATED);

            files.keySet().forEach(s -> writeJar(jar, s, files.get(s)));
            classes.forEach(classNode -> {
                final ClassWriter classWriter = new ClassWriter(0);
                classNode.accept(classWriter);
                writeJar(jar, classNode.name + ".class", classWriter.toByteArray());
            });
            jar.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void writeJar(JarOutputStream jar, String name, byte[] file) {
        try {
            jar.putNextEntry(new ZipEntry(name));
            jar.write(file);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void loadFiles(File input) throws Exception {
        final ZipInputStream zip = new ZipInputStream(new FileInputStream(input));
        ZipEntry zipEntry;

        while ((zipEntry = zip.getNextEntry()) != null) {
            if (!zipEntry.isDirectory() && !zipEntry.getName().endsWith(".class"))
                files.put(zipEntry.getName(), IOUtils.toByteArray(zip));
        }
    }

    private void loadClasses(File input) throws IOException {
        final JarFile jar = new JarFile(input);
        Stream<JarEntry> entryStream = jar.stream();
        entryStream.filter(entry -> isClass(jar, entry)).forEach(entry -> {
            try {
                final ClassReader classReader = new ClassReader(IOUtils.toByteArray(jar.getInputStream(entry)));
                final ClassNode classNode = new ClassNode();
                classReader.accept(classNode, ClassReader.EXPAND_FRAMES);

                classes.add(classNode);
            } catch (IOException e) {
                e.printStackTrace();
            }
        });
    }

    public boolean isClass(JarFile jar, JarEntry entry) {
        try {
            byte[] bytes = IOUtils.toByteArray(jar.getInputStream(entry));
            return entry.getName().endsWith(".class") && String.format("%02X%02X%02X%02X", bytes[0], bytes[1], bytes[2], bytes[3]).equalsIgnoreCase("cafebabe");
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
    }
}