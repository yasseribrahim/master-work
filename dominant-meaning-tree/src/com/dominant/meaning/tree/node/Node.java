/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.dominant.meaning.tree.node;

import com.dominant.meaning.tree.Bag;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.SortedSet;
import java.util.TreeSet;

/**
 *
 * @author Amar
 * @param <T>
 */
public class Node<T extends Bag> {

    private T value;
    private final SortedSet<Node<T>> childrens;
    private Node<T> parent;
    private final Comparator<?> comparator;

    public Node(T value, Node<T> parent, Comparator<?> comparator) {
        this.value = value;
        this.parent = parent;
        this.comparator = comparator;
        this.childrens = new TreeSet<Node<T>>(new NodeComparator<T>((Comparator<T>) comparator));
    }

    public T getValue() {
        return value;
    }

    public void setValue(T value) {
        this.value = value;
    }

    public List<Node<T>> getChildrens() {
        return new ArrayList<Node<T>>(childrens);
    }

    public String getChildrensAsString(String delimiter) {
        String chain = this.value.getWord();
        if (childrens != null) {
            for (int i = 0; i < childrens.size() - 1; i++) {
                chain += delimiter + childrens;
            }
        }
        return chain;
    }

//    public String getChildrensAsString(String delimiter, int top) {
//        String chain = (!"root".equalsIgnoreCase(this.value.getWord())) ? this.value.getWord() : "";
//        if (childrens != null) {
//            int index = 0;
//            Iterator<Node<T>> iterator = childrens.iterator();
//            while (iterator.hasNext() && (index < top)) {
//                chain += delimiter + iterator.next().value.getWord();
//                index++;
//            }
//        }
//        return chain;
//    }
    public String getChildrensAsString(String delimiter, int top) {
        String chain = "";
        int index = 0;
        Iterator<Node<T>> iterator = childrens.iterator();
        while (iterator.hasNext() && (index < top)) {
            chain += iterator.next().value.getWord();
            if (index < top - 1) {
                chain += delimiter;
            }
            index++;
        }
        return chain;
    }

    public Node<T> getParent() {
        return parent;
    }

    public void setParent(Node<T> parent) {
        this.parent = parent;
    }

    public Comparator<?> getComparator() {
        return comparator;
    }

    public Node<T> addChildren(T value) {
        Node<T> node = new Node<T>(value, this, comparator);
        return childrens.add(node) ? node : null;
    }

    public void addChildrens(T... values) {
        for (T t : values) {
            addChildren(t);
        }
    }

    public void repaire() {
        Node firstChildren = childrens.first();
        if (firstChildren.value.getWeight() > value.getWeight()) {
            childrens.remove(firstChildren);
            value = (T) firstChildren.value;
        }
    }

    public List<String> getDocuments(int topN) {
        List<String> documents = new ArrayList<String>();
        for (Node<T> node : childrens) {
            for (Map.Entry<String, Integer> entry : node.value.getDocuments()) {
                if (!documents.contains(entry.getKey())) {
                    documents.add(entry.getKey());
                }
            }
        }
        return documents;
    }

    public void printChildrens() {
        for (Node<T> node : childrens) {
            System.out.println(node.getValue());
        }
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof Node) {
            return false;
        }
        return ((Node<Bag>) obj).getValue().getWord().equalsIgnoreCase(this.getValue().getWord()); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public int hashCode() {
        int hash = 5;
        hash = 53 * hash + (this.value != null ? this.value.hashCode() : 0);
        return hash;
    }

    @Override
    public String toString() {
        return "Word: " + value.getWord() + ", Weight: " + value.getWeight();
    }
}
