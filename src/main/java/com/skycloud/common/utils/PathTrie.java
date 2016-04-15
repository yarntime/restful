/**
 * use path trie to handle request path
 */

package com.skycloud.common.utils;

import static java.util.Collections.emptyMap;
import static java.util.Collections.unmodifiableMap;

import java.util.HashMap;
import java.util.Map;

import com.skycloud.common.exception.RestException;

/**
 *
 */
public class PathTrie<T> {

    public interface Decoder {
        String decode(String value);
    }

    private final Decoder decoder;
    private final TrieNode root;
    private final char separator;
    private T rootValue;

    public PathTrie(Decoder decoder) {
        this('/', "*", decoder);
    }

    public PathTrie(char separator, String wildcard, Decoder decoder) {
        this.decoder = decoder;
        this.separator = separator;
        root = new TrieNode(new String(new char[] {separator}), null, wildcard);
    }

    public class TrieNode {
        private transient String key;
        private transient T value;
        private boolean isWildcard;
        private final String wildcard;

        private transient String namedWildcard;

        private Map<String, TrieNode> children;

        public TrieNode(String key, T value, String wildcard) {
            this.key = key;
            this.wildcard = wildcard;
            this.isWildcard = (key.equals(wildcard));
            this.value = value;
            this.children = emptyMap();
            if (isNamedWildcard(key)) {
                namedWildcard = key.substring(key.indexOf('{') + 1, key.indexOf('}'));
            } else {
                namedWildcard = null;
            }
        }

        public void updateKeyWithNamedWildcard(String key) {
            this.key = key;
            namedWildcard = key.substring(key.indexOf('{') + 1, key.indexOf('}'));
        }

        public boolean isWildcard() {
            return isWildcard;
        }

        public synchronized void addChild(TrieNode child) {
            addInnerChild(child.key, child);
        }

        private void addInnerChild(String key, TrieNode child) {
            Map<String, TrieNode> newChildren = new HashMap<String, TrieNode>(children);
            newChildren.put(key, child);
            children = unmodifiableMap(newChildren);
        }

        public TrieNode getChild(String key) {
            return children.get(key);
        }

        public synchronized void insert(String[] path, int index, T value) {
            if (index >= path.length)
                return;

            String token = path[index];
            String key = token;
            if (isNamedWildcard(token)) {
                key = wildcard;
            }
            TrieNode node = children.get(key);
            if (node == null) {
                T nodeValue = index == path.length - 1 ? value : null;
                node = new TrieNode(token, nodeValue, wildcard);
                addInnerChild(key, node);
            } else {
                if (isNamedWildcard(token)) {
                    node.updateKeyWithNamedWildcard(token);
                }

                // in case the target(last) node already exist but without a value
                // than the value should be updated.
                if (index == (path.length - 1)) {
                    assert (node.value == null || node.value == value);
                    if (node.value == null) {
                        node.value = value;
                    }
                }
            }

            node.insert(path, index + 1, value);
        }

        private boolean isNamedWildcard(String key) {
            return key.indexOf('{') != -1 && key.indexOf('}') != -1;
        }

        private String namedWildcard() {
            return namedWildcard;
        }

        private boolean isNamedWildcard() {
            return namedWildcard != null;
        }

        public String getValue(String[] path) {
            StringBuilder sb = new StringBuilder();
            for (int i = 0; i < path.length; ++i) {
                sb.append(path[i]);
                sb.append("/");
            }
            sb.deleteCharAt(sb.length() - 1);
            return sb.toString();
        }

        public T retrieve(String[] path, int index, Map<String, String> params) {
            if (index >= path.length) {
                throw new RestException("failed to retrieve path " + getValue(path));
            }

            String token = path[index];
            TrieNode node = children.get(token);
            boolean usedWildcard;
            if (node == null) {
                node = children.get(wildcard);
                if (node == null) {
                    throw new RestException("failed to retrieve path " + getValue(path));
                }
                usedWildcard = true;
            } else {
                // If we are at the end of the path, the current node does not have a value but
                // there
                // is a child wildcard node, use the child wildcard node
                if (index + 1 == path.length && node.value == null
                        && children.get(wildcard) != null) {
                    node = children.get(wildcard);
                    usedWildcard = true;
                } else {
                    usedWildcard = token.equals(wildcard);
                }
            }

            put(params, node, token);

            if (index == (path.length - 1)) {
                return node.value;
            }

            T res = node.retrieve(path, index + 1, params);
            if (res == null && !usedWildcard) {
                node = children.get(wildcard);
                if (node != null) {
                    put(params, node, token);
                    res = node.retrieve(path, index + 1, params);
                }
            }

            return res;
        }

        private void put(Map<String, String> params, TrieNode node, String value) {
            if (params != null && node.isNamedWildcard()) {
                String as = decoder.decode(value);
                params.put(node.namedWildcard(), as);
            }
        }

        @Override
        public String toString() {
            return key;
        }
    }

    public void insert(String path, T value) {
        String[] strings = StringUtils.splitStringToArray(path, separator);
        if (strings.length == 0) {
            rootValue = value;
            return;
        }
        int index = 0;
        // supports initial delimiter.
        if (strings.length > 0 && strings[0].isEmpty()) {
            index = 1;
        }
        root.insert(strings, index, value);
    }

    public T retrieve(String path) {
        return retrieve(path, null);
    }

    public T retrieve(String path, Map<String, String> params) {
        if (path.length() == 0) {
            return rootValue;
        }
        String[] strings = StringUtils.splitStringToArray(path, separator);
        if (strings.length == 0) {
            return rootValue;
        }
        int index = 0;
        // supports initial delimiter.
        if (strings.length > 0 && strings[0].isEmpty()) {
            index = 1;
        }
        return root.retrieve(strings, index, params);
    }
}
