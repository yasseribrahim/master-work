/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.search.engine.indexing;

import com.dataset.engine.CustomParser;
import com.dataset.engine.Dataset;
import com.dataset.engine.DatasetManager;
import com.dataset.engine.Naming;
import java.io.File;
import java.io.FileFilter;
import java.io.IOException;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.index.IndexWriter;

/**
 *
 * @author interactive
 */
public class CustomXmlIndexing extends Indexing {

    private final CustomParser parser;

    private CustomXmlIndexing(String workingDirectory, String datasetDirectory) {
        super(workingDirectory, datasetDirectory);
        dataset = Dataset.getInstance(workingDirectory, datasetDirectory, new FileFilter() {

            @Override
            public boolean accept(File pathname) {
                return (pathname.getName().toLowerCase().endsWith(".eng"));
            }
        });
        parser = CustomParser.getInstance();
    }

    public static CustomXmlIndexing getIndexing(String workingDirectory, String datasetDirectory) {
        return new CustomXmlIndexing(workingDirectory, datasetDirectory);
    }

    @Override
    public void buildIndex(float percentage) throws IOException {
        IndexWriter writer;
        int total = 0, limit;
        for (DatasetManager.Data data : dataset.getDataset()) {
            writer = util.getIndexWriter(data.getName());
            limit = (int) (data.getFiles().length * percentage);
            for (int i = 0; (i < data.getFiles().length && i < limit); i++) {
                File file = data.getFiles()[i];
                try {
                    addDocument(writer, createDocument(file));
                    System.out.println("Indexing File: " + file.getAbsolutePath());
                    total++;
                } catch (Exception ex) {
                    limit++;
                    System.err.println("*******Can't Indexing File: " + file.getAbsolutePath() + ", Message: " + ex);
                }
            }

            writer.commit();
            writer.close();
        }

        System.out.println("Total Index Files: " + total + " From: " + dataset.getSize() + ", Fail in: " + (dataset.getSize() - total));
    }

    @Override
    public void buildIndex() throws IOException {
        IndexWriter writer;
        int total = 0;
        for (DatasetManager.Data data : dataset.getDataset()) {
            writer = util.getIndexWriter(data.getName());
            for (File file : data.getFiles()) {
                try {
                    addDocument(writer, createDocument(file));
                    System.out.println("Indexing File: " + file.getAbsolutePath());
                    total++;
                } catch (Exception ex) {
                    System.err.println("*******Can't Indexing File: " + file.getAbsolutePath() + ", Message: " + ex);
                }
            }

            writer.commit();
            writer.close();
        }

        System.out.println("Total Index Files: " + total + " From: " + dataset.getSize() + ", Fail in: " + (dataset.getSize() - total));
    }

    public void buildPublicIndex() throws IOException {
        IndexWriter writer = util.getIndexWriter();

        int total = 0;
        for (DatasetManager.Data data : dataset.getDataset()) {

            for (File file : data.getFiles()) {
                try {
                    addDocument(writer, createDocument(file));
                    System.out.println("Indexing File: " + file.getAbsolutePath());
                    total++;
                } catch (Exception ex) {
                    System.err.println("*******Can't Indexing File: " + file.getAbsolutePath() + ", Message: " + ex);
                }
            }
        }
        writer.optimize();
        writer.commit();
        writer.close();

        System.out.println("Total Index Files: " + total + " From: " + dataset.getSize() + ", Fail in: " + (dataset.getSize() - total));
    }

    /**
     * Create a new document and write it to the given writer
     *
     * @param writer The writer to write out to
     * @param fields The searchable and data fields to write into doc
     * @throws IOException If there was problem writing doc
     */
    @Override
    public void addDocument(IndexWriter writer, Field... fields) throws IOException {
        // make a new, empty document
        Document document = new Document();
        // add other fields
        if (fields != null) {
            for (Field field : fields) {
                document.add(field);
            }
        }
        // write into index, assuming we are recreating every time
        writer.addDocument(document);
    }

    /**
     * Create a new document
     *
     * @param fields The searchable and data fields to write into doc
     * @return
     */
    @Override
    public Document createDocument(Field... fields) {
        // make a new, empty document
        Document document = new Document();
        // add other fields
        if (fields != null) {
            for (Field field : fields) {
                document.add(field);
            }
        }
        return document;
    }

    public Document createDocument(File file) throws Exception {
        CustomParser.Data data = parser.parse(file.getAbsolutePath());
        String title = data.getTitle();
        String description = data.getDescription();
        String notes = data.getNotes();
        String location = data.getLocation();
        String image = data.getImage();

        Field pathField = new Field(Naming.FIELD_PATH, file.getAbsolutePath(), Field.Store.YES, Field.Index.NO, Field.TermVector.NO);
//        Field titleField = new Field(Naming.FIELD_TITLE, title, Field.Store.NO, Field.Index.ANALYZED, Field.TermVector.YES);
        Field descriptionField = new Field(Naming.FIELD_DESCRIPTION, description, Field.Store.NO, Field.Index.ANALYZED, Field.TermVector.YES);
//        Field notesField = new Field(Naming.FIELD_NOTES, notes, Field.Store.NO, Field.Index.ANALYZED, Field.TermVector.YES);
//        Field locationField = new Field(Naming.FIELD_LOCATION, location, Field.Store.NO, Field.Index.ANALYZED, Field.TermVector.YES);
//        Field imageField = new Field(Naming.FIELD_IMAGE, image, Field.Store.YES, Field.Index.NO, Field.TermVector.NO);

        return createDocument(pathField, /*titleField,*/ descriptionField/*, notesField, locationField, imageField*/);
    }

    @Override
    public Field[] getFields(File file, String category) throws IOException {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
}
