package me.exeos.breversed.util;

import java.util.List;

public class ListUtil {

    public static <T> void move(List<T> list, int from, int to) {
        if (from == to) {
            return;
        }

        list.add((to > from ? to - 1 : to), list.remove(from));
    }
}
