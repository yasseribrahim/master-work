/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.search.engine.utilies;

import com.dataset.engine.DatasetManager;
import com.dataset.engine.FileManager;
import com.dominant.meaning.tree.Bag;
import com.dominant.meaning.tree.Exporter;
import com.dominant.meaning.tree.Tree;
import java.io.File;
import java.io.FilenameFilter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

/**
 *
 * @author interactive
 */
public class TreeValidated {

    private List<Trees> list;

    public TreeValidated() {
        list = new ArrayList<>();
    }

    public void validateTree(String workingDirectory, String datasetDirectory) throws Exception {
        File[] files = new File(workingDirectory + "/output").listFiles(new FilenameFilter() {
            @Override
            public boolean accept(File dir, String name) {
                return name.endsWith(".xml");
            }
        });
        Exporter exporter = new Exporter();
        Trees trees;
        List<CustomTree> customs;
        CustomTree custom;
        String[] words, words1;
        int counter = 1;
        for (File file : files) {
            customs = new ArrayList<>();
            words = getWords(file, exporter);
            System.out.println("File No.: " + counter);
            counter++;
            for (File file1 : files) {
                if (!file.getName().equalsIgnoreCase(file1.getName())) {
                    System.out.println("Compare: " + file.getName() + " by: " + file1.getName());
                    words1 = getWords(file1, exporter);

                    custom = new CustomTree(file1, compare(words, words1));
                    customs.add(custom);
                }
            }
            trees = new Trees(file, customs);
            trees.sort();
            list.add(trees);
        }

        Collections.sort(list, new Comparator<Trees>() {
            @Override
            public int compare(Trees o1, Trees o2) {
                for (int i = 0; i < o1.getTrees().size(); i++) {
                    if (o1.getTrees().get(i).value > o2.getTrees().get(i).value) {
                        return -1;
                    } else if (o1.getTrees().get(i).value < o2.getTrees().get(i).value) {
                        return 1;
                    }
                }

                return 0;
            }
        });

        list.get(list.size() - 1).print();
        System.out.println("||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||");
        list.get(0).print();
    }

    public String[] getWords(File file, Exporter exporter) {
        Tree<Bag> tree = exporter.importer(file.getAbsolutePath());
        return tree.getRoot().getChildrens().get(0).getChildrensAsString(" ", 100).split(" ");
    }

    public int compare(String[] words, String[] words1) {
        int counter = 0;
        for (int i = 0; i < words.length; i++) {
            for (int j = 0; j < words1.length; j++) {
                if (words[i].equalsIgnoreCase(words1[j])) {
                    counter++;
                    break;
                }
            }
        }
        return counter;
    }

    public static class Trees {

        private File tree;
        private List<CustomTree> trees;

        public Trees(File tree) {
            this(tree, new ArrayList<CustomTree>());
        }

        public Trees(File tree, List<CustomTree> trees) {
            this.tree = tree;
            this.trees = trees;
        }

        public void sort() {
            Collections.sort(getTrees(), new Comparator<CustomTree>() {
                @Override
                public int compare(CustomTree o1, CustomTree o2) {
                    return -1 * (o1.value - o2.value);
                }
            });
        }

        public File getTree() {
            return tree;
        }

        public void setTree(File tree) {
            this.tree = tree;
        }

        public List<CustomTree> getTrees() {
            return trees;
        }

        public void setTrees(List<CustomTree> trees) {
            this.trees = trees;
        }

        public void print() {
            StringBuilder builder = new StringBuilder();
            builder.append(tree.getName()).append("\n");
            for (CustomTree tree1 : trees) {
                builder.append(tree1.tree.getName()).append(": ").append(tree1.value).append("\n");
            }
            System.out.println(builder.toString());
        }
    }

    public static class CustomTree {

        private File tree;
        private int value;

        public CustomTree(File tree, int value) {
            this.tree = tree;
            this.value = value;
        }

        public File getTree() {
            return tree;
        }

        public void setTree(File tree) {
            this.tree = tree;
        }

        public int getValue() {
            return value;
        }

        public void setValue(int value) {
            this.value = value;
        }
    }

    public static void main(String[] args) throws Exception {
        String workingDir = "/home/interactive/Desktop/Master Work/Working Dir" + FileManager.FILE_SEPARATOR;
//        String datasetDir = "/home/interactive/Desktop/Master Work/Dataset/flickr-bmvc2009-metadata/images";
        String datasetDir = "/home/interactive/Desktop/Computer/Java Jars/Maining/iaprtc12/annotations_complete_eng";

        new TreeValidated().validateTree(workingDir, datasetDir);
    }
}
