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
public class ConceptDetails {
    private String id;
    private String term;
    private String document;
    private Integer frequent;
    private Concept conceptId;

    public ConceptDetails() {
    }

    public ConceptDetails(String id, String term, String document, Integer frequent) {
        this.id = id;
        this.term = term;
        this.document = document;
        this.frequent = frequent;
    }

    public ConceptDetails(String id) {
        this.id = id;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getTerm() {
        return term;
    }

    public void setTerm(String term) {
        this.term = term;
    }

    public String getDocument() {
        return document;
    }

    public void setDocument(String document) {
        this.document = document;
    }

    public Integer getFrequent() {
        return frequent;
    }

    public void setFrequent(Integer frequent) {
        this.frequent = frequent;
    }

    public Concept getConceptId() {
        return conceptId;
    }

    public void setConceptId(Concept conceptId) {
        this.conceptId = conceptId;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (id != null ? id.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof ConceptDetails)) {
            return false;
        }
        ConceptDetails other = (ConceptDetails) object;
        if ((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "javaapplication5.ConceptDetails[ id=" + id + " ]";
    }
    
    public static final String INSERT = "INSERT INTO concept_details VALUES(?, ?, ?, ?, ?)";
    public static final String SELECT = "SELECT * FROM concept_details WHERE id = ?";    
    public static final String SELECT_CONCEPT = "SELECT * FROM concept_details WHERE concept_id = ?";
    public static final String COUNT = "SELECT distinct document AS NO FROM concept_details where concept_id = ? and term in (TERMS);";
}
