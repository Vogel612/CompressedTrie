package de.vogel612.ct;

import java.util.*;

/**
 * Node to contain data for prefix-based searches.
 *
 * <p>A Node maintains a collection of child nodes, it's own prefix and the information whether it stores
 * a complete word. This means in a Trie that contains "box" and "boxes", you get following picture:</p>
 *
 * <tt>
 *     [box, true]
 *         |
 *     [es, true]
 * </tt>
 *
 * <p>Adding another word like "boxing" results in:</p>
 *
 * <tt>
 *          [box, true]
 *          /         \
 *     [es, true]   [ing, true]
 * </tt>
 *
 * <p>A node that's not a full word will only appear when you add "boxer"</p>
 *
 * <tt>
 *          [box, true]
 *          /         \
 *     [e, false]   [ing, true]
 *      /      \
 *  [s, true]  [r, true]
 * </tt>
 *
 * Created by vogel612 on 02.10.15.
 */
class TrieNode {
    final String prefix;
    boolean isCompleteWord;
    final Set<TrieNode> children = new LinkedHashSet<>();

    public TrieNode(String prefix) {
        // assume true
        this.prefix = prefix;
        this.isCompleteWord = true;
    }

    public TrieNode(String prefix, boolean isCompleteWord, Collection<TrieNode> children) {
        this.prefix = prefix;
        this.isCompleteWord = isCompleteWord;
        this.children.addAll(children);
    }

    @Override
    public boolean equals(Object other) {
        if (!(other instanceof TrieNode)) {
            return false;
        }
        TrieNode node = (TrieNode) other;
        return node == this || node.prefix.equals(this.prefix);
    }
}
