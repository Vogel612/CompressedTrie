package de.vogel612.ct;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

import java.util.Collections;

/**
 * Created by vogel612 on 02.10.15.
 */
public class TrieNodeTests {
    @Test
    public void equalityRespects_prefixOnly() {
        TrieNode one = new TrieNode("test");
        TrieNode other = new TrieNode("test");
        assertTrue(one.equals(other));
    }

    @Test
    public void equalityOnlyCompares_trieNodes() {
        TrieNode one = new TrieNode("test");
        String other = "test";
        assertFalse(one.equals(other));
    }

    @Test
    public void equality_respectsCase(){
        TrieNode one = new TrieNode("test");
        TrieNode other = new TrieNode("Test");
        assertFalse(one.equals(other));
    }

    @Test
    public void equality_ignoresWordChecks() {
        TrieNode one = new TrieNode("test", false, Collections.emptyList());
        TrieNode other = new TrieNode("test");
        assertTrue(one.equals(other));
    }
}
