package de.vogel612.ct;

import static de.vogel612.util.StringHelper.longestCommonPrefix;

import java.util.*;
import java.util.function.Predicate;

/**
 * Node to contain data for prefix-based searches.
 * <p>
 * <p>A Node maintains a collection of child nodes, it's own prefix and the information whether it stores
 * a complete word. This means in a Trie that contains "box" and "boxes", you get following picture:</p>
 * <p>
 * <pre>
 * [box, true]
 *     |
 * [es, true]
 * </pre>
 * <p>
 * <p>Adding another word like "boxing" results in:</p>
 * <p>
 * <pre>
 *      [box, true]
 *      /         \
 * [es, true]   [ing, true]
 * </pre>
 * <p>
 * <p>A node that's not a full word will only appear when you add "boxer"</p>
 * <p>
 * <pre>
 *           [box, true]
 *           /         \
 *      [e, false]   [ing, true]
 *       /      \
 * [s, true]  [r, true]
 * </pre>
 * <p>
 * Created by vogel612 on 02.10.15.
 */
class TrieNode {
    final String prefix;
    boolean isCompleteWord;
    final Set<TrieNode> children = new LinkedHashSet<>();

    static Predicate<TrieNode> prefixMatching(String newString) {
        return child -> newString.startsWith(child.prefix);
    }

    static Predicate<TrieNode> splitNodeMatching(String newString) {
        return child -> child.prefix.startsWith(newString);
    }

    public TrieNode(final String prefix) {
        Objects.requireNonNull(prefix, "Cannot create TrieNode without prefix");
        this.prefix = prefix;
        // assume true
        this.isCompleteWord = true;
    }

    public TrieNode(final String prefix, final boolean isCompleteWord, final Collection<TrieNode> children) {
        this.prefix = prefix;
        this.isCompleteWord = isCompleteWord;
        this.children.addAll(children);
    }

    /**
     * Finds and returns the first child matching the given condition
     *
     * @param condition The condition that needs to apply to the child
     *
     * @return An Optional containing the Child.
     */
    Optional<TrieNode> matchingChild(Predicate<TrieNode> condition) {
        Objects.requireNonNull(condition, "Cannot find prefixes for null strings");
        return children.stream()
          .filter(condition)
          .findFirst();
    }

    /**
     * Add a child to this Node. If applicable the adding process is delegated to matching child nodes.
     * Also child nodes will be created / split where applicable to have only one or zero child nodes matching any
     * given
     * prefix.
     *
     * @param newString The String to add as child into the subtree with root <tt>this</tt>. For relevant propagation,
     *                  this string will be shortened and given to the next matching child, effectively traversing the
     *                  Trie
     */
    public void addChild(final String newString) {
        Objects.requireNonNull(newString, "Cannot add a null string");
        Optional<TrieNode> prefixChild = matchingChild(prefixMatching(newString));
        if (prefixChild.isPresent()) {
            final TrieNode node = prefixChild.get();
            prefixPresent(newString, node);
        } else {
            prefixMissing(newString);
        }
    }

    /**
     * Handles adding a child to this node when the {@link #prefixMatching(String)}
     * does not apply to any children.
     * <p>
     * Checks for {@link #splitNodeMatching(String)} children to split and maintain or adds a new Child
     *
     * @param newString The String data that need to be added
     */
    private void prefixMissing(String newString) {
        // need to check for children with longer, but matching prefix
        Optional<TrieNode> existingChild = matchingChild(splitNodeMatching(newString));
        if (existingChild.isPresent()) {
            // split the prefix
            TrieNode splitChild = existingChild.get();
            String suffix = splitChild.prefix.substring(newString.length());
            final TrieNode keeper = new TrieNode(suffix, splitChild.isCompleteWord, splitChild.children);
            children.remove(splitChild);
            children.add(new TrieNode(newString, true, Collections.singleton(keeper)));
        } else {
            // check for common substring children (only one should exist)
            Map<String, TrieNode> currentMatch = findSubstringMatch(newString);
            String newPrefix = currentMatch.keySet().iterator().next();
            final TrieNode oldChild = currentMatch.get(newPrefix);
            children.remove(oldChild);

            final TrieNode newChild = buildNewChild(newPrefix, oldChild, newString);
            children.add(newChild);
        }
    }

    private Map<String, TrieNode> findSubstringMatch(String newString) {
        Map<String, TrieNode> currentMatch = Collections.singletonMap("", null);
        for (TrieNode child : children) {
            String lcp = longestCommonPrefix(child.prefix, newString);
            if (lcp.length() == 0) {
                continue;
            }
            // there should only be one match, since matched prefixes are handed down
            currentMatch = Collections.singletonMap(lcp, child);
            break;
        }
        return currentMatch;
    }

    /**
     * Builds a new Child from a given old Child, a prefix and a String that's still to be added to the subtree of
     * <tt>oldChild</tt>
     *
     * @param newPrefix The smallest common prefix of newString and oldChild.prefix, also serving as prefix for the new
     *                  Node
     * @param oldChild  The old child node, either null, if no matching childnode is found, or a node that needs to be
     *                  split.
     * @param newString The remainder of the String to be added to the subtree. Includes the common prefix
     *
     * @return The newly built child node to replace oldChild in the Tree
     *
     * @apiNote This new child keeps connections of the old child intact through
     * {@link TrieNode#TrieNode(String, boolean, Collection)}
     */
    private TrieNode buildNewChild(String newPrefix, TrieNode oldChild, String newString) {
        if (newPrefix.equals("")) { // no common prefix found
            return new TrieNode(newString);
        } else {
            final TrieNode keeper = new TrieNode(oldChild.prefix.substring(newPrefix.length()),
              oldChild.isCompleteWord, oldChild.children);
            final TrieNode insertNode = new TrieNode(newString.substring(newPrefix.length()));
            return new TrieNode(newPrefix, false, Arrays.asList(keeper, insertNode));
        }
    }

    /**
     * Handles adding a child to this node when the {@link #prefixMatching(String)} returned a matching Child
     * <p>
     * If the child node's prefix and the word to add match, the node is marked as complete word, else the
     * word to add is cut and handed down to the node for adding.
     *
     * @param newString The Content to add to the subtree below this node
     * @param node      The child node with matching prefix
     */
    private void prefixPresent(String newString, TrieNode node) {
        if (node.prefix.length() == newString.length()) {
            // if the prefix of the child and the remaining String are equally long,
            // the node is already matching the word
            node.isCompleteWord = true;
        } else {
            // cut the prefix and hand it down again
            node.addChild(newString.substring(node.prefix.length()));
        }
    }

    /**
     * finds the subtree matching a given prefix while maintaining the traversed
     * nodes of the prefix-tree by recursively invoking itself on the matching children.
     *
     * @param currentWord     The current word as coming from traversal state
     * @param remainingPrefix The remaining prefix to be matched
     *
     * @return A singleton-map containing the subtree's root node and the word associated with the traversal
     */
    Map<TrieNode, String> findMatchingSubtree(final String currentWord, final String remainingPrefix) {
        if (remainingPrefix.equals("")) {
            return Collections.singletonMap(this, currentWord);
        }

        Optional<TrieNode> matchingChild = matchingChild(prefixMatching(remainingPrefix));
        if (!matchingChild.isPresent()) {
            // Check for partial prefix match
            Optional<TrieNode> existingChild = matchingChild(splitNodeMatching(remainingPrefix));
            if (existingChild.isPresent()) {
                return Collections.singletonMap(existingChild.get(), currentWord + existingChild.get().prefix);
            }
            return Collections.singletonMap(null, currentWord);
        }
        TrieNode child = matchingChild.get();
        if (child.prefix.equals(remainingPrefix)) {
            return Collections.singletonMap(child, currentWord + remainingPrefix);
        }
        return child.findMatchingSubtree(currentWord + child.prefix, remainingPrefix.substring(child.prefix.length()));
    }

    /**
     * Recursively traverses through a subtree, adding all words found to a given collection.
     * This includes the current node
     *
     * @param currentWord The current word in traversal state
     * @param out         The collection to contain the results
     */
    void subtreeWordNodes(String currentWord, Collection<String> out) {
        if (isCompleteWord) {
            out.add(currentWord);
        }
        for (TrieNode child : children) {
            child.subtreeWordNodes(currentWord + child.prefix, out);
        }
    }

    @Override
    public boolean equals(Object other) {
        if (!(other instanceof TrieNode)) {
            return false;
        }
        TrieNode node = (TrieNode) other;
        return node == this || node.prefix.equals(this.prefix);
    }

    @Override
    public int hashCode() {
        return prefix.hashCode();
    }
}
