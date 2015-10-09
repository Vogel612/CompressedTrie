package de.vogel612.ct;

import static de.vogel612.ct.TrieNode.prefixMatching;

import java.util.*;
import java.util.function.BinaryOperator;

public class CompressedTrie implements Collection<String> {

    private final TrieNode root = new TrieNode("");

    private int size;

    public CompressedTrie() {
    }

    public CompressedTrie(Collection<String> items) {
        addAll(items);
    }

    public boolean add(String newString) {
        if (contains(newString)) {
            return false;
        }
        root.addChild(newString);
        size++;
        return true;
    }

    @Override
    public boolean retainAll(Collection<?> collection) {
        return false;
    }

    @Override
    public void clear() {
        removeAll(matches(""));
    }

    @Override
    public boolean addAll(Collection<? extends String> items) {
        return items.stream().map(this::add).reduce((b1, b2) -> b1 && b2).orElse(false);
    }

    @Override
    public boolean remove(Object word) {
        Objects.requireNonNull(word, "Cannot look for a null word");
        if (!(word instanceof String)) {
            throw new ClassCastException("Trie is only intended for Strings");
        }
        TrieNode wordNode = findWordNode((String)word, root);
        if (wordNode != null) {
            // FIXME purge orphaned childnodes
            wordNode.isCompleteWord = false;
            size--;
            return true;
        }
        return false;
    }

    @Override
    public boolean removeAll(Collection<?> words) {
        boolean result = true;
        for (Object word : words) {
            result &= remove(word);
        }
        return result;
    }

    @Override
    public boolean contains(Object o) {
        if (o instanceof String) {
            return contains((String) o);
        }
        return false;
    }

    public boolean contains(String word) {
        return findWord(word);
    }

    @Override
    public boolean containsAll(Collection<?> words) {
        for (Object word : words) {
            if (!contains(word)) {
                return false;
            }
        }
        return true;
    }

    /**
     * Checks whether any of the items in the given collection is in the Trie
     *
     * @param words The collection to check for items
     *
     * @return A boolean if (and only if) for any item in the given collection i, an item o in the Trie exists so that
     * <tt>o.equals(i)</tt>
     */
    public boolean containsAny(Collection<?> words) {
        for (Object word : words) {
            if (contains(word)) {
                return true;
            }
        }
        return false;
    }

    /**
     * Returns a list of matches from this Trie where a match is a word, that has the same prefix
     * as the given prefix. There are no guarantees to the order of the list.
     *
     * @param prefix The prefix that the word has to begin with to be considered a match.
     *
     * @return A List of matches.
     */
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

    @Override
    public int size() {
        return size;
    }

    @Override
    public boolean isEmpty() {
        return size == 0;
    }

    @Override
    public Iterator<String> iterator() {
        return matches("").iterator();
    }

    @Override
    public Object[] toArray() {
        return matches("").toArray();
    }

    @Override
    public <T> T[] toArray(T[] ts) {
        return matches("").toArray(ts);
    }
}
