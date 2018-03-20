/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.dominant.meaning.tree;

import com.dominant.meaning.tree.node.Node;
import com.dominant.meaning.tree.node.NodeVisitor;

/**
 *
 * @author interactive
 * @param <T>
 */
public class DefaultNodeVisitor<T extends Bag> implements NodeVisitor<T> {

    @Override
    public boolean visit(final Node<T> node) {
        StringBuilder sb = new StringBuilder();
        Node<T> current = node;
        do {
            if (sb.length() > 0) {
                sb.insert(0, " > ");
            }
            sb.insert(0, String.valueOf(current.getValue()));
            current = current.getParent();
        } while (current != null);
        System.out.println(sb);
        return true;
    }
}
