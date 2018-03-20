/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.search.engine.indexing;

import com.dataset.engine.Dataset;
import com.search.engine.utilies.IndexingUtil;
import java.io.File;
import java.io.IOException;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.index.IndexWriter;

/**
 *
 * @author interactive
 */
public abstract class Indexing {

    protected int indexPercentage = 100;
    protected Dataset dataset;
    protected IndexingUtil util;

    protected Indexing(String workingDirectory, String datasetDirectory) {
        dataset = Dataset.getInstance(workingDirectory, datasetDirectory);
        util = IndexingUtil.getInstance(workingDirectory, datasetDirectory);
    }

    public abstract void buildIndex() throws IOException;
    public abstract void buildIndex(float percentage) throws IOException;

    public String cleanup(String text) {
        text = text.replaceAll("(\\s)+", " ").trim(); // cleanup whitespace
        text = text.replaceAll("[0-9]", "").trim(); // cleanup numeric digit
        return text;
    }

    /**
     * Create a new document and write it to the given writer
     *
     * @param writer The writer to write out to
     * @param document
     * @throws IOException If there was problem writing doc
     */
    public void addDocument(IndexWriter writer, Document document) throws IOException {
        // write into index, assuming we are recreating every time
        writer.addDocument(document);
    }

    public abstract void addDocument(IndexWriter writer, Field... fields) throws IOException;

    public abstract Document createDocument(Field... fields) throws IOException;
    
    public abstract Field[] getFields(File file, String category) throws IOException;

    /**
     * delete all indexing file from hard disk
     */
    public void clear() {
        dataset.getManager().clearIndexer();
    }
    
    /**
     * delete all public indexing file from hard disk
     */
    public void clearPublic() {
        dataset.getManager().clearPublicIndexer();
    }
}
