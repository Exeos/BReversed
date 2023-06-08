package xyz.breversed.api.utils;

import lombok.experimental.UtilityClass;

@UtilityClass
public class CharUtil {

    public boolean containsUnAlphabetic(String input) {
        for (char c : input.toCharArray()) {
            if (!Character.isLetterOrDigit(c)) {
                System.out.println(c);
                return true;
            }
        }
        return false;
    }
}