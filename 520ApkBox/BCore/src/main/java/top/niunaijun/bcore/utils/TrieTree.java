package top.niunaijun.bcore.utils;

import java.util.LinkedList;
import java.util.List;
import java.util.Objects;

public class TrieTree {
    private final TrieNode root = new TrieNode();

    private static class TrieNode {
        char content;
        String word;
        boolean isEnd = false;
        final List<TrieNode> children = new LinkedList<>();

        public TrieNode() { }

        public TrieNode(char content, String word) {
            this.content = content;
            this.word = word;
        }

        @Override
        public boolean equals(Object object) {
            if (object instanceof TrieNode) {
                return ((TrieNode) object).content == content;
            }
            return false;
        }

        public TrieNode nextNode(char content) {
            for (TrieNode childNode : children) {
                if (childNode.content == content) {
                    return childNode;
                }
            }
            return null;
        }
    }

    public void add(String word) {
        TrieNode current = root;
        StringBuilder wordBuilder = new StringBuilder();

        for (int index = 0; index < word.length(); ++index) {
            char content = word.charAt(index);
            wordBuilder.append(content);

            TrieNode node = new TrieNode(content, wordBuilder.toString());
            if (Objects.requireNonNull(current).children.contains(node)) {
                current = current.nextNode(content);
            } else {
                current.children.add(node);
                current = node;
            }

            if (index == (word.length() - 1)) {
                Objects.requireNonNull(current).isEnd = true;
            }
        }
    }

    public String search(String word) {
        TrieNode current = root;
        for (int index = 0; index < word.length(); ++index) {
            char content = word.charAt(index);

            TrieNode node = new TrieNode(content, null);
            if (current.children.contains(node)) {
                current = current.nextNode(content);
            } else {
                return null;
            }

            if (Objects.requireNonNull(current).isEnd) {
                return current.word;
            }
        }
        return null;
    }
}
