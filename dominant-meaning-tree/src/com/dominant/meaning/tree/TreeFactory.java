/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.dominant.meaning.tree;

import com.dominant.meaning.tree.node.Node;
import java.util.Comparator;

/**
 *
 * @author Amar
 */
public class TreeFactory {

    public static void main(String... args) {
        Tree<Bag> tree = new Tree<Bag>(new Bag("Root", 1), new Comparator<Bag>() {

            @Override
            public int compare(Bag o1, Bag o2) {
                return (o1.getWeight() <= o2.getWeight()) ? 1 : -1;
            }
        }, null);
        
        Node<Bag> root = tree.getRoot();
        Node<Bag> child1, child2;
        for (int i = 0; i < 10; i++) {
            child1 = root.addChildren(new Bag("Word " + i, i));
            for (int j = 0; j < 10; j++) {
                child2 = child1.addChildren(new Bag("Word " + i + "-" + j, j));
                for (int k = 0; k < 10; k++) {
                    child2.addChildren(new Bag("Word " + i + "-" + j + "-" + k, k));
                }
            }
        }
//tree.visitNodes();
        tree.deapthFirstSearch(root, "Word 9-9-0").printChildrens();
//        tree.visitNodes();
//        SortedSet<Node<Bag>> childrens = tree.childrens("Word 1");
//        for (Node<Bag> node : childrens) {
//            System.out.println(node);
//        }
    }
}
