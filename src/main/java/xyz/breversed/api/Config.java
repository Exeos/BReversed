package xyz.breversed.api;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import org.reflections.Reflections;
import org.reflections.scanners.SubTypesScanner;
import xyz.breversed.BReversed;
import xyz.breversed.api.asm.transformer.Transformer;

import java.io.FileReader;
import java.util.ArrayList;

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
        {
            path = configObject.get("path").getAsString();
            jars[0] = configObject.get("input").getAsString() + (configObject.get("input").getAsString().endsWith(".jar") ? "" : ".jar");
            jars[1] = configObject.get("output").getAsString() + (configObject.get("output").getAsString().endsWith(".jar") ? "" : ".jar");
        }

        renamerStr = configObject.get("renamerString").getAsString();

        /* Adding transformers by their class's simple name */
        {
            JsonArray transformers = configObject.get("transformers").getAsJsonArray();
            ArrayList<String> actives = new ArrayList<>(new Gson().fromJson(transformers, ArrayList.class));

            Reflections reflections = new Reflections("xyz.breversed.transformers", new SubTypesScanner(false));
            reflections.getSubTypesOf(Transformer.class).stream().filter(aClass -> actives.contains(aClass.getSimpleName())).forEach(aClass -> {
                try {
                    BReversed.INSTANCE.transformerManager.transformers.add(aClass.newInstance());
                } catch (InstantiationException | IllegalAccessException e) {
                    throw new RuntimeException(e);
                }
            });
        }
    }

    public String getPath() {
        return path.isEmpty() ? path : path.endsWith("\\") ? path : path.contains("\\") ? path + "\\" : path + "/";
    }

    public enum Task {
        DETECT, TRANSFORM
    }
}