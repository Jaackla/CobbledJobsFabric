package io.github.adainish.cobbledjobsfabric.util;



import java.awt.*;
import java.util.*;
import java.util.List;

public class RandomHelper
{

    public static final Random rand = new Random();

    public RandomHelper() {
    }


    /** @deprecated */
    public static <T> T getRandomElementFromList(List<T> list) {
        return getRandomElementFromCollection(list);
    }

    public static <T> T getRandomElementFromCollection(Collection<T> collection) {
        if (collection.isEmpty()) {
            return null;
        } else {
            int index = rand.nextInt(collection.size());
            Iterator<T> iterator = collection.iterator();

            for(int i = 0; i < index; ++i) {
                iterator.next();
            }

            return iterator.next();
        }
    }
}
