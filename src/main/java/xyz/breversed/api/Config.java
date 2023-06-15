package xyz.breversed.api;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import org.reflections.Reflections;
import org.reflections.scanners.SubTypesScanner;
import org.reflections.util.ClasspathHelper;
import org.reflections.util.ConfigurationBuilder;
import xyz.breversed.BReversed;
import xyz.breversed.api.asm.transformer.Transformer;
import xyz.breversed.api.asm.transformer.comparator.CompTarget;

import java.io.FileReader;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Set;
import java.util.concurrent.locks.Condition;
import java.util.function.Predicate;

public class Config {

    public Task task = Task.DETECT;

    private String path;
    public final String[] jars = new String[2];

    public String renamerStr;


    public void loadConfig() throws Exception {
        FileReader configFile = new FileReader("config.json");
        JsonObject configObject = JsonParser.parseReader(configFile).getAsJsonObject();

        task = Task.valueOf(configObject.get("task").getAsString());

        /* jar to target and jar export to */

        path = configObject.get("path").getAsString();
        jars[0] = configObject.get("input").getAsString() + (configObject.get("input").getAsString().endsWith(".jar") ? "" : ".jar");
        jars[1] = configObject.get("output").getAsString() + (configObject.get("output").getAsString().endsWith(".jar") ? "" : ".jar");


        renamerStr = configObject.get("renamerString").getAsString();

        JsonArray transformers = configObject.get("transformers").getAsJsonArray();
        ArrayList<String> actives = new ArrayList<>(new Gson().fromJson(transformers, ArrayList.class));

        /* Scrape all sub packages in xyz.breversed.transformers and add to HashMap<String, ArrayList<Transformer>> */
        scrape();
        /* Adding transformers, if input is "package/" add all transformers in that package */
        for (String active : actives) {
            String[] split = active.replace(".", "/").split("/");
            scanAndAdd(split[0], split.length == 1 ? "" : split[1]);
        }
    }

    private void scanAndAdd(String subPackage, String className) {
        Reflections reflections = new Reflections("xyz.breversed.transformers." + subPackage, new SubTypesScanner(false));
        for (Class<? extends Transformer> aClass : reflections.getSubTypesOf(Transformer.class)) {
            if (className.isEmpty() || className.equals(aClass.getSimpleName())) {
                try {
                    /* Dupe check, might remove in future when you have to use the same transformer more than once */
                    if (BReversed.INSTANCE.transformerManager.transformerMap.get(subPackage).stream().filter(transformer -> transformer.getClass() == aClass).findFirst().orElse(null) == null)
                        BReversed.INSTANCE.transformerManager.transformerMap.get(subPackage).add(aClass.newInstance());
                } catch (InstantiationException | IllegalAccessException e) {
                    throw new RuntimeException(e);
                }
            }
        }
    }

    private void scrape() {
        Reflections reflections = new Reflections("xyz.breversed.transformers", new SubTypesScanner(false));
        for (Class<?> aClass : reflections.getSubTypesOf(Transformer.class)) {
            String[] split = aClass.getPackageName().split("\\.");
            if (!BReversed.INSTANCE.transformerManager.transformerMap.containsKey(split[split.length - 1]))
                BReversed.INSTANCE.transformerManager.transformerMap.put(split[split.length - 1], new ArrayList<>());
        }
    }

    public String getPath() {
        return path.isEmpty() ? path : path.endsWith("\\") ? path : path.contains("\\") ? path + "\\" : path + "/";
    }

    public enum Task {
        DETECT, TRANSFORM
    }
}