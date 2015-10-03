package de.vogel612.util;

/**
 * Created by vogel612 on 03.10.15.
 */
public class StringHelper {

    private StringHelper() {
    }

    /**
     * Returns the longest common prefix for two Strings or <tt>""</tt> if no prefix is shared
     *
     * @param one The one String
     * @param two The other String
     *
     * @return The longest common prefix, or ""
     */
    public static String longestCommonPrefix(String one, String two) {
        if (one.equals(two)) { return one; }
        if (one.equals("") || two.equals("")) {
            return "";
        }

        String currentPrefix = "";
        int size = 0;
        while (one.startsWith(currentPrefix) && two.startsWith(currentPrefix)) {
            size++;
            currentPrefix = one.substring(0, size);
        }
        return one.substring(0, size - 1);
    }
}
