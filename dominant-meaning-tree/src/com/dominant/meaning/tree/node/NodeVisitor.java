/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.dominant.meaning.tree.node;

import com.dominant.meaning.tree.Bag;

/**
 *
 * @author Amar
 * @param <T>
 */
public interface NodeVisitor<T extends Bag> {

    public boolean visit(Node<T> node);
}
