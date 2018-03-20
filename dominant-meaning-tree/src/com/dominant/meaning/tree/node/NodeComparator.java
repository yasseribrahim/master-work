/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.dominant.meaning.tree.node;

import com.dominant.meaning.tree.Bag;
import java.util.Comparator;

/**
 *
 * @author Amar
 * @param <T>
 */
public class NodeComparator<T extends Bag> implements Comparator<Node<T>> {

    private final Comparator<T> comparator;

    public NodeComparator(Comparator<T> comparator) {
        this.comparator = comparator;
    }

    @Override
    public int compare(Node<T> node1, Node<T> node2) {
        return comparator.compare(node1.getValue(), node2.getValue());
    }
}
