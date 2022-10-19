package xyz.breversed.api.detection;

import org.reflections.Reflections;
import org.reflections.scanners.SubTypesScanner;
import xyz.breversed.api.interfaces.IMethods;

public class Detector implements IMethods {

    public void detect() {
        final Reflections reflections = new Reflections("xyz.breversed.detectors", new SubTypesScanner(false));
        reflections.getSubTypesOf(AbstractDetector.class).forEach(detectorClass -> {
            try {
                final AbstractDetector detector = detectorClass.newInstance();
                if (detector.detect())
                    System.out.println("Detected " + detector.getClass().getSimpleName());
            } catch (InstantiationException | IllegalAccessException e) {
               System.out.println("Error adding Detectors:");
               e.printStackTrace();
            }
        });
    }
}
