package utils;

import java.util.LinkedList;
import java.util.List;

/**
 * Created by Michaël Ludmann on 6/10/15.
 */
public class Split {
    /**
     * Split a String around a byte separator, within a given limit
     *
     * @param separator byte separator
     * @param input     String to split
     * @param limit     max number of elements allowed in the returned list
     *                  If limit is < 0, then proceed as if there were no limit
     * @return list of bytes arrays, its size being of maximum {limit} elements
     * if limit = 0: returns empty list
     * if limit = 1: returns the first element result of the split (i.e.
     * a list of one byte[] stopping at the first separator)
     */
    public static List<String> split(final char separator, final String input, final int limit) {
        final List<String> list = new LinkedList<>();
        if (0 == limit) {
            return list;
        }

        StringBuilder stringBuilder = new StringBuilder();
        int position = 0;
        final boolean limited = 0 < limit;
        while (position < input.length() && (!limited || limit > list.size())) {
            if (separator == input.charAt(position)) {
                list.add(stringBuilder.toString());
                stringBuilder.setLength(0); // reset the builder
            } else {
                stringBuilder.append(input.charAt(position));
            }
            position++;
        }
        if (1 != limit)
            list.add(stringBuilder.toString());
        return list;
    }

    public static List<String> split(final char separator, final String input) {
        return split(separator, input, -1);
    }
}
