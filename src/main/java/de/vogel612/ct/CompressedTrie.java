package de.vogel612.ct;

import static de.vogel612.ct.TrieNode.prefixMatching;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * Created by vogel612 on 02.10.15.
 */
public class CompressedTrie {

    private final TrieNode root = new TrieNode("");

    public CompressedTrie() {
    }

    public CompressedTrie(Collection<String> items) {
        addAll(items);
    }

    public void add(String newString) {
        root.addChild(newString);
    }

    public void addAll(Collection<String> items) {
        items.stream().forEach(this::add);
    }

    public boolean remove(String word) {
        return removeWord(word);
    }

    public boolean removeAll(Collection<String> words) {
        boolean result = true;
        for (String word : words) {
            result &= remove(word);
        }
        return result;
    }

    public boolean contains(String word) {
        return findWord(word);
    }

    public boolean containsAll(Collection<String> words) {
        for (String word : words) {
            if (!contains(word)) {
                return false;
            }
        }
        return true;
    }

    public boolean containsAny(Collection<String> words) {
        for (String word : words) {
            if (contains(word)) {
                return true;
            }
        }
        return false;
    }

    public List<String> matches(String prefix) {
        Map<TrieNode, String> subtreeData = root.findMatchingSubtree("", prefix);
        TrieNode subtree = subtreeData.keySet().iterator().next(); // only one entry
        if (subtree == null) {
            return Collections.emptyList();
        }
        List<String> matches = new LinkedList<>();
        if (subtree.isCompleteWord) {
            matches.add(subtreeData.get(subtree));
        }
        subtree.subtreeWordNodes(subtreeData.get(subtree), matches);
        return matches;
    }

    /**
     * Searches for the given word in the subtree.
     * The word must match with the prefix of the current node.
     *
     * @param word The word (including this node's prefix) to search
     *
     * @return a flag indicating whether the word matches the subtree
     */
    private boolean findWord(final String word) {
        Objects.requireNonNull(word, "Cannot look for a null word");
        TrieNode wordNode = findWordNode(word, root);
        return wordNode != null && wordNode.isCompleteWord;
    }

    private boolean removeWord(final String word) {
        Objects.requireNonNull(word, "Cannot look for a null word");
        TrieNode wordNode = findWordNode(word, root);
        if (wordNode != null) {
            // FIXME purge orphaned childnodes
            wordNode.isCompleteWord = false;
            return true;
        }
        return false;
    }

    private TrieNode findWordNode(final String word, final TrieNode tree) {
        Objects.requireNonNull(word, "Cannot look for a null word");
        if (word.equals(tree.prefix)) {
            return tree;
        }
        if (word.startsWith(tree.prefix)) {
            final String subtreeWord = word.substring(tree.prefix.length());
            Optional<TrieNode> subtree = tree.matchingChild(prefixMatching(subtreeWord));
            if (subtree.isPresent()) {
                return findWordNode(subtreeWord, subtree.get());
            }
        }
        return null;
    }
}
