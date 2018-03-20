/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.search.engine.dominant.meaning.database;

import java.util.List;

/**
 *
 * @author interactive
 */
public class Concept {
    private String id;
    private String value;
    private String name;
    private List<ConceptDetails> details;

    public Concept() {
    }

    public Concept(String id, String value, String name) {
        this.id = id;
        this.value = value;
        this.name = name;
    }
    
    

    public Concept(String id) {
        this.id = id;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<ConceptDetails> getConceptDetailsList() {
        return details;
    }

    public void setConceptDetailsList(List<ConceptDetails> conceptDetailsList) {
        this.details = conceptDetailsList;
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
        if (!(object instanceof Concept)) {
            return false;
        }
        Concept other = (Concept) object;
        if ((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "javaapplication5.Concept[ id=" + id + " ]";
    }
    
    public static final String INSERT = "INSERT INTO concept VALUES(?, ?, ?)";
    public static final String SELECT = "SELECT * FROM concept WHERE id = ?";
    public static final String SELECT_ALL = "SELECT * FROM concept";
    public static final String UPDATE = "UPDATE concept SET name = ? WHERE value = ?";
}
