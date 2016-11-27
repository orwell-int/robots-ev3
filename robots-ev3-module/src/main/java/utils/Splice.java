package utils;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Michaël Ludmann on 21/06/15.
 */
public class Splice {
    public static List<String> subList(List<String> list, int fromIndex, int toIndex) {
        ArrayList<String> arrayList = new ArrayList<>();

        for (int i = fromIndex; i < toIndex; i++) {
            arrayList.add(list.get(i));
        }
        return arrayList;
    }
}
