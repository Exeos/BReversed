package xyz.breversed.core.api.asm.detection;

import org.reflections.Reflections;
import org.reflections.scanners.SubTypesScanner;

public class Detector {

    public void detect() {
        Reflections reflections = new Reflections("xyz.breversed.core.detectors", new SubTypesScanner(false));
        reflections.getSubTypesOf(AbstractDetector.class).forEach(detectorClass -> {
            try {
                AbstractDetector detector = detectorClass.newInstance();
                if (detector.detect()) {
                    System.out.println("Detected " + detector);
                    if (!detector.context.isEmpty()) {
                        System.out.println("{");
                        for (String context : detector.context) {
                            System.out.println("\t" + context);
                        }
                        System.out.println("}");
                    }
                }
            } catch (InstantiationException | IllegalAccessException e) {
               System.out.println("Error adding Detectors:");
               e.printStackTrace();
            }
        });
    }
}
