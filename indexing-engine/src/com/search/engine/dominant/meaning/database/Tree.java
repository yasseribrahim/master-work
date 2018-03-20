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
public class Tree {
    private String id;
    private String concept;
    private String conceptName;
    private String term;
    private float weight;

    public Tree(String concept, String conceptName, String term, float weight) {
        this(null, concept, conceptName, term, weight);
    }
    
    public Tree(String id, String concept, String conceptName, String term, float weight) {
        this.id = id;
        this.concept = concept;
        this.conceptName = conceptName;
        this.term = term;
        this.weight = weight;
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

    public String getTerm() {
        return term;
    }

    public void setTerm(String term) {
        this.term = term;
    }

    public float getWeight() {
        return weight;
    }

    public void setWeight(float weight) {
        this.weight = weight;
    }
    
    public static final String INSERT = "INSERT INTO tree VALUES(?, ?, ?, ?, ?)";
    public static final String SELECT = "SELECT * FROM tree WHERE concept = ? ORDER BY weight DESC";
}
