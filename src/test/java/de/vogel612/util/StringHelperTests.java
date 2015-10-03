package de.vogel612.util;

import static de.vogel612.util.StringHelper.longestCommonPrefix;
import static org.junit.Assert.assertEquals;

import org.junit.Test;

/**
 * Created by vogel612 on 03.10.15.
 */
public class StringHelperTests {

    private final String[][] cases = new String[][]{
      {"test", "tester", "testing"},
      {"", "asdf", "bla"},
      {"box", "boxer", "boxing"},
      {"bo", "box", "boss"},
      {"blue", "blue", "blue"},
      {"", "", "bla"},
      {"", "bla", ""}
    };

    @Test
    public void runTestCases() {
        for (String[] testCase : cases) {
            assertEquals(testCase[0], longestCommonPrefix(testCase[1], testCase[2]));
        }
    }
}
