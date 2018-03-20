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
public class Frequent {

    private String concept;
    private String termId;
    private String term;
    private Integer frequent;
    private Integer count;

    public Frequent(String concept, String termId, String term, Integer frequent, Integer count) {
        this.concept = concept;
        this.termId = termId;
        this.term = term;
        this.frequent = frequent;
        this.count = count;
    }

    public String getConcept() {
        return concept;
    }

    public void setConcept(String concept) {
        this.concept = concept;
    }

    public String getTermId() {
        return termId;
    }

    public void setTermId(String termId) {
        this.termId = termId;
    }

    public String getTerm() {
        return term;
    }

    public void setTerm(String term) {
        this.term = term;
    }

    public Integer getFrequent() {
        return frequent;
    }

    public void setFrequent(Integer frequent) {
        this.frequent = frequent;
    }

    public Integer getCount() {
        return count;
    }

    public void setCount(Integer count) {
        this.count = count;
    }

    @Override
    public String toString() {
        return "[Concept: " + concept + ", Term: " + term + ", Freq: " + frequent + ", Count: " + count + "]";
    }
    
    public static final String SELECT = "SELECT * FROM frequents WHERE concept = ? ORDER BY frequent DESC";
}
