package de.vogel612.ct;

import static org.junit.Assert.assertTrue;

import org.junit.Before;
import org.junit.Test;

/**
 * Created by vogel612 on 02.10.15.
 */
public class TrieTests {

    CompressedTrie cut;

    @Before
    public void setup() {
        cut = new CompressedTrie();
    }

    @Test
    public void emptyTrie_returnsNoMatches() {
        assertTrue(cut.matches("asdf").isEmpty());
    }
}
