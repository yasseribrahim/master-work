/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.dominant.meaning.tree;

import com.dominant.meaning.tree.node.Node;
import com.dominant.meaning.tree.node.NodeVisitor;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 *
 * @author Amar
 * @param <T>
 */
public class Tree<T extends Bag> {

    private final Node<T> root;
    private Comparator<T> comparator;
    private NodeVisitor<T> visitor;
    private final NodeVisitor<T> NATURAL_NODE_VISITOR = new DefaultNodeVisitor<T>();
    private final Comparator<T> NATURAL_ORDER = new Comparator<T>() {

        @Override
        public int compare(T t1, T t2) {
            return (t1.getWeight() <= t2.getWeight()) ? 1 : -1;
        }
    };

    public Tree(T value, Comparator<T> comparator, NodeVisitor<T> visitor) {
        this.comparator = (comparator != null) ? comparator : NATURAL_ORDER;
        this.visitor = (visitor != null) ? visitor : NATURAL_NODE_VISITOR;
        this.root = new Node<T>(value, null, this.comparator);
    }

    public Node<T> getRoot() {
        return root;
    }

    public Comparator<?> getComparator() {
        return comparator;
    }

    public void setComparator(Comparator<T> comparator) {
        this.comparator = comparator;
    }

    public void setNodeVisitor(NodeVisitor<T> nodeVisitor) {
        this.visitor = nodeVisitor;
    }

    public void visitNodes() {
        doVist(root);
    }

    public boolean doVist(Node<T> node) {
        boolean result = visitor.visit(node);

        if (result) {
            for (Node<T> children : node.getChildrens()) {
                if (!doVist(children)) {
                    result = false;
                    break;
                }
            }
        }

        return result;
    }

    public String dominantMeaningSearch(String query, int top) {
        if (query == null) {
            return null;
        }

        Map<Node<T>, Integer> mapper = new HashMap<Node<T>, Integer>();
        String[] words = query.split(" ");
        Node<T> node;
        for (String word : words) {
            node = traverse(word);
            if (mapper.containsKey(node)) {
                mapper.put(node, mapper.get(node) + 1);
            } else {
                mapper.put(node, 1);
            }
        }

        // select main concept
        node = null;
        int max = Integer.MIN_VALUE;
        for (Map.Entry<Node<T>, Integer> entry : mapper.entrySet()) {
            if (max < entry.getValue().intValue()) {
                max = entry.getValue();
                node = entry.getKey();
            }
        }

        return ((node != null) ? node.getChildrensAsString(" ", top) : query);
    }

    public Node<T> traverse(String word) {
        List<Node<T>> searched = new ArrayList<Node<T>>();
        Node<T> node, searchedNode;
        double maxhit;
        searched.add(root);

        if (root.getValue().getWord().equalsIgnoreCase(word)) {
            return root;
        }

        searchedNode = null;
        maxhit = Double.MIN_VALUE;
        while (!searched.isEmpty()) {
            // step 1
            node = searched.get(0);
            searched.remove(0);

            // step 2
            if (node.getValue().getWord().equalsIgnoreCase(word)) {
                if (node.getParent() != null && (maxhit < node.getValue().getWeight())) {
                    maxhit = node.getValue().getWeight();
                    searchedNode = node.getParent();
                }
            } else { // step 3
                if (node.getChildrens() != null) {
                    searched.addAll(0, node.getChildrens());
                }
            }
        }

        return searchedNode;
    }

    public Node<T> deapthFirstSearch(Node<T> node, String value) {
        Node<T> result = null;
        boolean visited = visitor.visit(node);

        if (node.getValue().getWord().equals(value)) {
            return node;
        }

        if (visited) {
            for (Node<T> children : node.getChildrens()) {
                if ((result = deapthFirstSearch(children, value)) != null) {
                    break;
                }
            }
        }

        return result;
    }

    public String expandQuery(String word, int top) {
        Node<Bag> node = (Node<Bag>) deapthFirstSearch(root, word);
        return (node != null) ? node.getChildrensAsString("+", top) : word;
    }

    public void print() {
        StringBuilder builder = new StringBuilder();
        builder.append("Root: ").append(root.getValue().getWord()).append(", Weight: ").append(root.getValue().getWeight());
        for (Node<T> node : root.getChildrens()) {
            builder.append("\n\t").append("Concept: ").append(node.getValue().getWord()).append(", Weight: ").append(node.getValue().getWeight());

            for (Node<T> children : node.getChildrens()) {
                builder.append("\n\t\t").append("Word: ").append(children.getValue().getWord()).append(", Weight: ").append(children.getValue().getWeight());
            }
        }

        System.out.println(builder.toString());
    }

    public void print(int max) {
        StringBuilder builder = new StringBuilder();
        builder.append("Root: ").append(root.getValue().getWord()).append(", Weight: ").append(root.getValue().getWeight());
        Node<T> children;
        int counter;
        for (Node<T> node : root.getChildrens()) {
            builder.append("\n\t").append("Concept: ").append(node.getValue().getWord()).append(", Weight: ").append(node.getValue().getWeight());

            counter = 0;
            while (counter < max && counter < node.getChildrens().size()) {
                children = node.getChildrens().get(counter);
                builder.append("\n\t\t").append("Word: ").append(children.getValue().getWord()).append(", Weight: ").append(children.getValue().getWeight());
                counter++;
            }
        }

        System.out.println(builder.toString());
    }
}
