package de.vogel612.ct;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;

import org.junit.Before;
import org.junit.Test;

import java.util.Arrays;
import java.util.Collection;
import java.util.List;

/**
 * Created by vogel612 on 02.10.15.
 */
public class TrieTests {

    private CompressedTrie cut;

    @Before
    public void setup() {
        cut = new CompressedTrie();
    }

    @Test
    public void emptyTrie_returnsNoMatches() {
        assertTrue(cut.matches("asdf").isEmpty());
    }

    @Test
    public void filledTree_containsWord() {
        cut.add("test");
        assertTrue(cut.contains("test"));
    }

    @Test
    public void filledTree_containsAllWords() {
        cut.add("asdf");
        cut.add("test");
        cut.add("random");
        assertTrue(cut.containsAll(Arrays.asList("test", "random")));
    }

    @Test
    public void filledTree_containsAny() {
        cut.add("asdf");
        cut.add("test");
        cut.add("random");
        assertTrue(cut.containsAny(Arrays.asList("foo", "bar", "test")));
    }

    @Test
    public void filledTree_containsNone() {
        cut.add("asdf");
        cut.add("test");
        cut.add("random");
        assertFalse(cut.containsAny(Arrays.asList("foo", "bar", "quux")));
    }

    @Test
    public void filledTree_containsSome() {
        cut.add("asdf");
        cut.add("test");
        cut.add("random");
        Collection<String> testItems = Arrays.asList("foo", "bar", "quux", "test");
        assertFalse(cut.containsAll(testItems));
        assertTrue(cut.containsAny(testItems));
    }

    @Test
    public void removeNode_changesContains() {
        cut.add("test");
        assertTrue(cut.contains("test"));
        boolean result = cut.remove("test");
        assertFalse(cut.contains("test"));
        assertTrue(result);
    }

    @Test
    public void removeNonexistingNode_returnsFalse() {
        assertFalse(cut.remove("random"));
    }

    @Test
    public void removeAll_removesAllRelevantNodes() {
        Collection<String> items = Arrays.asList("test", "foo", "bar", "quux");
        cut.addAll(items);
        assertTrue(cut.containsAll(items));
        boolean result = cut.removeAll(items);
        assertFalse(cut.containsAny(items));
        assertTrue(result);
    }

    @Test
    public void removeAll_returnsFalse_onMissingNodes() {
        cut.add("test");
        assertTrue(cut.contains("test"));
        boolean result = cut.removeAll(Arrays.asList("test", "random"));
        assertFalse(cut.contains("test"));
        assertFalse(result);
    }

    @Test
    public void copyConstructor() {
        Collection<String> items = Arrays.asList("test", "foo", "bar", "quux");
        cut = new CompressedTrie(items);
        assertTrue(cut.containsAll(items));
    }

    @Test
    public void matchesTesting() {
        cut.add("test");
        cut.add("testing");
        cut.add("twitter");
        cut.add("twerk");

        Collection<String> expected = Arrays.asList("test", "testing", "twitter", "twerk");
        List<String> actual = cut.matches("t");
        assertTrue(actual.containsAll(expected));

        expected = Arrays.asList("test", "testing");
        actual = cut.matches("tes");
        assertTrue(actual.containsAll(expected));

        expected = Arrays.asList("twitter", "twerk");
        actual = cut.matches("tw");
        assertTrue(actual.containsAll(expected));
    }
}
