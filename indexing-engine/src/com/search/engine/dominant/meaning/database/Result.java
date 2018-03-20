/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.search.engine.dominant.meaning.database;

/**
 *
 * @author interactive
 */
public class Result {

    private String id;
    private String concept;
    private String conceptName;
    private int size;
    private int hit;
    private int expected;
    private int retrieved;
    private float precision;
    private float recall;
    private float f1;

    public Result(String id, String concept, String conceptName, int size, int hit, int expected, int retrieved, float precision, float recall, float f1) {
        this.id = id;
        this.concept = concept;
        this.conceptName = conceptName;
        this.size = size;
        this.hit = hit;
        this.expected = expected;
        this.retrieved = retrieved;
        this.precision = precision;
        this.recall = recall;
        this.f1 = f1;
    }

    public Result(String concept, String conceptName, int size, int hit, int expected, int retrieved, float precision, float recall, float f1) {
        this(null, concept, conceptName, size, hit, expected, retrieved, precision, recall, f1);
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getConcept() {
        return concept;
    }

    public void setConcept(String concept) {
        this.concept = concept;
    }

    public String getConceptName() {
        return conceptName;
    }

    public void setConceptName(String conceptName) {
        this.conceptName = conceptName;
    }

    public int getSize() {
        return size;
    }

    public void setSize(int size) {
        this.size = size;
    }

    public int getHit() {
        return hit;
    }

    public void setHit(int hit) {
        this.hit = hit;
    }

    public int getExpected() {
        return expected;
    }

    public void setExpected(int expected) {
        this.expected = expected;
    }

    public int getRetrieved() {
        return retrieved;
    }

    public void setRetrieved(int retrieved) {
        this.retrieved = retrieved;
    }

    public float getPrecision() {
        return precision;
    }

    public void setPrecision(float precision) {
        this.precision = precision;
    }

    public float getRecall() {
        return recall;
    }

    public void setRecall(float recall) {
        this.recall = recall;
    }

    public void setF1(float f1) {
        this.f1 = f1;
    }

    public float getF1() {
        return f1;
    }

    @Override
    public String toString() {
        return "INSERT INTO result VALUES(" + id + ", " + concept + ", " + conceptName + ", " + size + ", " + hit + ", " + expected + ", " + retrieved + ")";
    }

    public static final String INSERT = "INSERT INTO result VALUES(?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
}
