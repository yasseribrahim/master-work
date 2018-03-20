/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.search.engine.indexing;

import com.dataset.engine.DatasetManager;
import com.dataset.engine.FileManager;
import com.dataset.engine.Naming;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.index.IndexWriter;
import org.apache.tika.exception.TikaException;
import org.apache.tika.metadata.Metadata;
import org.apache.tika.parser.AutoDetectParser;
import org.apache.tika.parser.ParseContext;
import org.apache.tika.sax.BodyContentHandler;
import org.xml.sax.ContentHandler;
import org.xml.sax.SAXException;

/**
 *
 * @author interactive
 */
public class HtmlIndexing extends Indexing {

    private final List<File> unindexFiles = new ArrayList<>();

    private HtmlIndexing(String workingDirectory, String datasetDirectory) {
        super(workingDirectory, datasetDirectory);
    }

    public static HtmlIndexing getIndexing(String workingDirectory, String datasetDirectory) {
        return new HtmlIndexing(workingDirectory, datasetDirectory);
    }

    @Override
    public void buildIndex() throws IOException {
        clear();
        IndexWriter writer;
        Field[] fields;
        int limit = 0;

        int total = 0;
        for (DatasetManager.Data data : dataset.getDataset()) {
            writer = util.getIndexWriter(data.getName());

            limit = (int) (data.getFiles().length * (indexPercentage / 100.0));
            for (int i = 0; (i < data.getFiles().length && i < limit); i++) {
                File file = data.getFiles()[i];
                try {
                    fields = getFields(file, data.getName());
                    if (fields != null) {
                        addDocument(writer, fields);
                        System.out.println("Indexing File: " + file.getAbsolutePath());
                        total++;
                    }
                } catch (Exception ex) {
                    unindexFiles.add(file);
                    System.err.println("xxxxxxxxx Un Indexing File: " + file.getAbsolutePath() + ex);
                }
            }

            writer.optimize();
            writer.commit();
            writer.close();
        }

        for (File file : unindexFiles) {
            file.delete();
        }
        System.out.println("Total Index Files: " + total + " From: " + dataset.getSize());
    }

    public void buildPublicIndex() throws IOException {
        clearPublic();
        IndexWriter writer;
        Field[] fields;
        int limit = 0;
        int total = 0;

        writer = util.getIndexWriter();
        for (DatasetManager.Data data : dataset.getDataset()) {

            limit = (int) (data.getFiles().length * (indexPercentage / 100.0));
            for (int i = 0; (i < data.getFiles().length && i < limit); i++) {
                File file = data.getFiles()[i];
                try {
                    fields = getFields(file, data.getName());
                    if (fields != null) {
                        addDocument(writer, fields);
                        System.out.println("Indexing File: " + file.getAbsolutePath());
                        total++;
                    }
                } catch (Exception ex) {
                    unindexFiles.add(file);
                    System.err.println("xxxxxxxxx Un Indexing File: " + file.getAbsolutePath() + ex);
                }
            }
        }
        writer.optimize();
        writer.commit();
        writer.close();

        for (File file : unindexFiles) {
            file.delete();
        }
        System.out.println("Total Index Files: " + total + " From: " + dataset.getSize());
    }

    /**
     * Clean HTML, removing all tags and un-escaping so that we can index it
     * cleanly
     *
     * @param html The html to clean
     * @return cleaned html
     */
    @Override
    public String cleanup(String html) {
        html = html.replaceAll("(\\s)+", " ").trim(); // cleanup whitespace
        html = html.replaceAll("[0-9]", "").trim(); // cleanup numeric digit
        return html;
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
     * @throws IOException If there was problem writing doc
     */
    @Override
    public Document createDocument(Field... fields) throws IOException {
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

    public static void main(String... args) throws Exception {
//        Indexing indexing = getIndexing("/home/interactive/Desktop/Master Work/Working Dir" + FileManager.FILE_SEPARATOR, "/home/interactive/Desktop/Master Work/dataset");
//        indexing.clear();
//        ((HtmlIndexing) indexing).buildPublicIndex();
//        indexing.buildIndex();
        String file = "/home/interactive/Desktop/Computer/Java Jars/Maining/dataset/Bands/43";
        StringBuilder content;
        ContentHandler textHandler = new BodyContentHandler(10 * 1024 * 1024);
        Metadata metadata = new Metadata();
        AutoDetectParser parser = new AutoDetectParser();
        ParseContext context = new ParseContext();
        parser.parse(new FileInputStream(file), textHandler, metadata, context);
        content = new StringBuilder(textHandler.toString());
        System.out.println(content.toString());
    }

    @Override
    public Field[] getFields(File file, String category) throws IOException {
        try {
            StringBuilder content;
            ContentHandler textHandler = new BodyContentHandler(10 * 1024 * 1024);
            Metadata metadata = new Metadata();
            AutoDetectParser parser = new AutoDetectParser();
            ParseContext context = new ParseContext();
            parser.parse(dataset.getManager().getInputStream(file), textHandler, metadata, context);
            content = new StringBuilder(cleanup(textHandler.toString()));
            Field pathField = new Field(Naming.FIELD_PATH, file.getAbsolutePath(), Field.Store.YES, Field.Index.NO);
            Field categoryField = new Field(Naming.FIELD_CATEGORY, category, Field.Store.YES, Field.Index.ANALYZED);
            Field contentField = new Field(Naming.FIELD_CONTENT, content.toString().replaceAll("[^\\p{ASCII}]", ""), Field.Store.NO, Field.Index.ANALYZED);

            return new Field[]{pathField, contentField, categoryField};
        } catch (SAXException | TikaException ex) {
            System.err.println("xxxxxxxxx Un Indexing File: " + file.getAbsolutePath() + ex);
        }

        return null;
    }

    @Override
    public void buildIndex(float percentage) throws IOException {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
}
