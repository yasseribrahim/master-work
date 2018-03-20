/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.dominant.meaning.tree;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map;

/**
 *
 * @author interactive
 */
public class Bag {

    private String word;
    private float weight;
    private List<Map.Entry<String, Integer>> documents;

    public Bag(String word, float weight) {
        this.word = word;
        this.weight = weight;
        this.documents = new ArrayList<Map.Entry<String, Integer>>();
    }

    public String getWord() {
        return word;
    }

    public float getWeight() {
        return weight;
    }

    public void setWord(String word) {
        this.word = word;
    }

    public void setWeight(float weight) {
        this.weight = weight;
    }

    public List<Map.Entry<String, Integer>> getDocuments() {
        return documents;
    }

    public void setDocuments(List<Map.Entry<String, Integer>> documents) {
        Collections.sort(documents, new Comparator<Map.Entry<String, Integer>>() {

            @Override
            public int compare(Map.Entry<String, Integer> o1, Map.Entry<String, Integer> o2) {
                return o2.getValue() - o1.getValue();
            }
        });
        this.documents = documents;
    }

    @Override
    public String toString() {
        return word + ", Weight: " + weight;
    }
}
