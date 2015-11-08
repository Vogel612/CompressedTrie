package de.vogel612.ct;

import static de.vogel612.ct.TrieNode.prefixMatching;

import java.util.*;

public class CompressedTrie implements Collection<String> {

    private final TrieNode root = new TrieNode("", false, Collections.emptyList());

    // it's quicker to keep it here than calculating every time
    private int size;

    public CompressedTrie() {
    }

    public CompressedTrie(Collection<String> items) {
        addAll(items);
    }

    /**
     * Adds another String to this instance of CompressedTrie
     *
     * @param newString The new String to add to this instance
     *
     * @return true, if the collection was modified as a result of this method
     */
    public boolean add(String newString) {
        if (contains(newString)) {
            return false;
        }
        root.addChild(newString);
        size++;
        return true;
    }

    /**
     * Removes all elements but those in the given Collection from this instance of {@link CompressedTrie}.
     *
     * @param collection The Items to <b>not</b> remove
     *
     * @return true, if the collection changed from invoking this method, else false
     */
    @Override
    public boolean retainAll(Collection<?> collection) {
        return matches("").stream()
          .filter(e -> !collection.contains(e))
          .map(this::remove)
          .reduce((b1, b2) -> b1 || b2)
          .orElse(false);
    }

    /**
     * Removes all elements from this collection.
     */
    @Override
    public void clear() {
        root.children.clear();
        root.isCompleteWord = false;
        size = 0;
    }

    /**
     * Adds all elements of a given collection to this instance
     *
     * @param items The items to add to the collection
     *
     * @return true, if the collection changed from invoking this method, false otherwise
     */
    @Override
    public boolean addAll(Collection<? extends String> items) {
        return items.stream()
          .map(this::add)
          .reduce((b1, b2) -> b1 || b2)
          .orElse(false);
    }

    /**
     * Removes an item from this collection
     *
     * @param word The word to remove from the collection
     *
     * @return true, if the collection changed from invoking this method, false otherwise
     */
    @Override
    public boolean remove(Object word) {
        Objects.requireNonNull(word, "Cannot look for a null word");
        if (!(word instanceof String)) {
            return false; // throwing is a jerk move :D
        }
        // find a way to get to the parent??
        TrieNode wordNode = findWordNode((String) word, root);
        if (wordNode != null) {
            // FIXME purge orphaned childnodes
            wordNode.isCompleteWord = false;
            size--;
            return true;
        }
        return false;
    }

    /**
     * Removes all elements in the given collection from this collection.
     *
     * @param words The collection of elements to remove
     *
     * @return true, if the collection changed from invoking this method, false otherwises
     */
    @Override
    public boolean removeAll(Collection<?> words) {
        boolean result = false;
        for (Object word : words) {
            result |= remove(word);
        }
        return result;
    }

    /**
     * Checks whether this collection contains the given item, or to be more correct whether there is an item
     * <tt>k</tt>
     * so that <tt>o.equals(k);</tt> is true.
     *
     * @param o The item to check for
     *
     * @return true or false, indicating whether such an item has been found or not
     */
    @Override
    public boolean contains(Object o) {
        if (o instanceof String) {
            return contains((String) o);
        }
        return false;
    }

    /**
     * Checks whether this collection contains the given String, or more formally:whether there is an item
     * <tt>k</tt>
     * so that <tt>o.equals(k);</tt> is true.
     *
     * @param word The item to check for
     *
     * @return true or false, indicating whether such an item has been found or not
     */
    public boolean contains(String word) {
        return findWord(word);
    }

    /**
     * Checks whether this collection contains <b>all</b> of the items in a given Collection
     *
     * @param words The items that all have to be in this instance
     *
     * @return true or false, depending on whether all items have been found or not
     *
     * @implNote Will "short-circuit" on the first element not found
     */
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

    /**
     * Returns the size of the current collection
     *
     * @return The internally cached size as int
     *
     * @implNote works with a separate counter incremented when elements are successfully added and decremented when
     * elements are removed
     */
    @Override
    public int size() {
        return size;
    }

    @Override
    public boolean isEmpty() {
        return size == 0;
    }

    /**
     * Returns the iterator over the current state of the {@link CompressedTrie}.
     *
     * @return The Iterator over the current elements
     *
     * @implNote The Iterator is populated eagerly, this means all items are calculated before the iterator is
     * available
     */
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
