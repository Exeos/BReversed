package xyz.breversed.core.api.utils;

import lombok.experimental.UtilityClass;

@UtilityClass
public class CharUtil {

    public boolean containsUnAlphabetic(String input) {
        for (char c : input.toCharArray()) {
            if (!Character.isLetterOrDigit(c))
                return true;
        }
        return false;
    }
}