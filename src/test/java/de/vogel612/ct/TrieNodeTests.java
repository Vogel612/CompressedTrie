package de.vogel612.ct;

import static org.junit.Assert.*;

import org.junit.Test;

import java.util.*;

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
        //noinspection EqualsBetweenInconvertibleTypes
        assertFalse(one.equals(other));
    }

    @Test
    public void equality_respectsCase() {
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

    @Test
    public void addChild_forMissingPrefix() {
        TrieNode pseudoRoot = new TrieNode("");
        pseudoRoot.addChild("test");
        assertTrue(pseudoRoot.children.contains(new TrieNode("test")));
    }

    @Test
    public void addChild_forExistingPrefix() {
        TrieNode[] bottomNodes = new TrieNode[]{
          new TrieNode("ss"), new TrieNode("x")
        };
        TrieNode subtree = new TrieNode("bo", false, Arrays.asList(bottomNodes));
        TrieNode pseudoRoot = new TrieNode("", false, Collections.singleton(subtree));
        assertFalse(pseudoRoot.children.iterator().next().isCompleteWord);

        pseudoRoot.addChild("bo");
        assertTrue(pseudoRoot.children.iterator().next().isCompleteWord);
    }

    @Test
    public void buildTree() {
        TrieNode root = new TrieNode("");
        root.addChild("box");
        root.addChild("boxes");

        assertTrue(root.children.size() == 1);
        final TrieNode node = root.children.iterator().next();
        assertEquals(node.prefix, "box");
        assertTrue(node.children.size() == 1);
        assertTrue(node.isCompleteWord);
        final TrieNode leaf = node.children.iterator().next();
        assertEquals(leaf.prefix, "es");
        assertTrue(leaf.children.size() == 0);
        assertTrue(leaf.isCompleteWord);

        root.addChild("boxing");
        for (TrieNode leaflet : node.children) {
            assertTrue(leaflet.children.size() == 0);
            assertTrue(leaflet.isCompleteWord);
            assertTrue(leaflet.prefix.equals("es") || leaflet.prefix.equals("ing"));
        }
    }

    @Test
    public void buildDifferentTree() {
        TrieNode root = new TrieNode("");
        root.addChild("boxes");
        root.addChild("boxing");

        assertTrue(root.children.size() == 1);
        final TrieNode node = root.children.iterator().next();
        assertEquals(node.prefix, "box");
        assertFalse(node.isCompleteWord);
        assertTrue(node.children.size() == 2);
        for (TrieNode leaf : node.children) {
            assertTrue(leaf.children.size() == 0);
            assertTrue(leaf.isCompleteWord);
            assertTrue(leaf.prefix.equals("es") || leaf.prefix.equals("ing"));
        }
    }

    @Test
    public void prefixSplittingTree() {
        TrieNode root = new TrieNode("");
        root.addChild("boxes");
        root.addChild("box");

        assertTrue(root.children.size() == 1);
        final TrieNode node = root.children.iterator().next();
        assertEquals(node.prefix, "box");
        assertTrue(node.children.size() == 1);
        assertTrue(node.isCompleteWord);
        final TrieNode leaf = node.children.iterator().next();
        assertEquals(leaf.prefix, "es");
        assertTrue(leaf.children.size() == 0);
        assertTrue(leaf.isCompleteWord);
    }

    @Test
    public void subtreeWordNodes() {
        TrieNode root = new TrieNode("");
        root.addChild("box");
        root.addChild("boxes");

        TrieNode subnode = root.children.iterator().next();
        Collection<String> subtreeData = new ArrayList<>();
        subnode.subtreeWordNodes("asd", subtreeData);
        assertTrue(subtreeData.contains("asdes"));
        assertTrue(subtreeData.contains("asd"));

        subtreeData.clear();
        root.subtreeWordNodes("", subtreeData);
        assertTrue(subtreeData.contains("box"));
        assertTrue(subtreeData.contains("boxes"));
    }

    @Test
    public void findSubtreeNode() {
        TrieNode root = new TrieNode("");
        root.addChild("box");
        root.addChild("boxes");

        TrieNode boxNode = root.children.iterator().next();
        TrieNode esNode = boxNode.children.iterator().next();

        Map<TrieNode, String> results = root.findMatchingSubtree("", "b");
        assertSame(boxNode, results.keySet().iterator().next());
        assertEquals("box", results.values().iterator().next());

        results = root.findMatchingSubtree("", "boxe");
        assertSame(esNode, results.keySet().iterator().next());
        assertEquals("boxes", results.values().iterator().next());
    }

    @Test
    public void emptySubtreeNode() {
        TrieNode root = new TrieNode("");

        Map<TrieNode, String> results = root.findMatchingSubtree("test", "b");
        assertTrue(results.keySet().iterator().next() == null);
        assertEquals("test", results.values().iterator().next());
    }
}
