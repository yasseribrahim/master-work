/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.search.engine.utilies;

import com.dataset.engine.DatasetManager;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.AbstractMap;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.CharArraySet;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.index.TermDocs;
import org.apache.lucene.index.TermEnum;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;
import org.apache.lucene.util.Version;

/**
 *
 * @author interactive
 */
public class IndexingUtil {

    public static CharArraySet ENGLISH_STOP_WORDS;
    private final DatasetManager manager;
    private int counter = 0;

    static {
        List<String> list = new ArrayList<>();
        ENGLISH_STOP_WORDS = new CharArraySet(Version.LUCENE_32, list, true);
        try {
            BufferedReader reader = new BufferedReader(new FileReader("stopwords.en"));
            String line;
            while ((line = reader.readLine()) != null) {
                list.add(line.trim());
            }
            ENGLISH_STOP_WORDS = new CharArraySet(Version.LUCENE_32, list, false);
        } catch (IOException ex) {
            System.err.println(ex);
        }
    }

    public static IndexingUtil getInstance(String workingDirectory, String datasetDirectory) {
        return new IndexingUtil(workingDirectory, datasetDirectory);
    }

    private IndexingUtil(String workingDirectory, String datasetDirectory) {
        manager = DatasetManager.getManager(datasetDirectory, workingDirectory);
    }

    public IndexWriter getIndexWriter() throws IOException {
        Directory directory = FSDirectory.open(manager.getPublicIndexerDirectory());
        Analyzer analyzer = new StandardAnalyzer(Version.LUCENE_32, ENGLISH_STOP_WORDS);
        IndexWriterConfig config = new IndexWriterConfig(Version.LUCENE_32, analyzer);
        config.setOpenMode(IndexWriterConfig.OpenMode.CREATE);
        IndexWriter writer = new IndexWriter(directory, config);

        return writer;
    }

    public IndexReader getIndexReader() throws IOException {
        Directory directory = FSDirectory.open(manager.getPublicIndexerDirectory());
        IndexReader reader = IndexReader.open(directory);

        return reader;
    }

    public IndexWriter getIndexWriter(String name) throws IOException {
        Directory directory = FSDirectory.open(manager.getIndexerDirectory(name));
        Analyzer analyzer = new StandardAnalyzer(Version.LUCENE_32, ENGLISH_STOP_WORDS);
        IndexWriterConfig config = new IndexWriterConfig(Version.LUCENE_32, analyzer);
        config.setOpenMode(IndexWriterConfig.OpenMode.CREATE);
        IndexWriter writer = new IndexWriter(directory, config);

        return writer;
    }

    public IndexReader getIndexReader(String name) throws IOException {
        Directory directory = FSDirectory.open(manager.getIndexerDirectory(name));
        IndexReader reader = IndexReader.open(directory);

        return reader;
    }

    public void printIndex(String out) throws Exception {
        for (DatasetManager.Data data : manager.getDatas()) {
            printIndex(data.getName(), out);
        }
    }

    public void printConceptWithNoDocuments(String out) throws Exception {
        for (DatasetManager.Data data : manager.getDatas()) {
            IndexReader reader = getIndexReader(data.getName());
            System.out.println(data.getName() + "\t" + reader.numDocs());
        }
    }

    public void printIndex(String name, String out) throws Exception {
        IndexReader reader = getIndexReader(name);
        TermEnum terms = reader.terms();
        TermDocs docuemts;
        java.util.List<Map.Entry<String, Integer>> data = new java.util.ArrayList<>();
        PrintWriter writer = new PrintWriter(new FileWriter(out, true));
        StringBuilder builder = new StringBuilder();
        builder.append(++counter).append("\n");
        while (terms.next()) {
            docuemts = reader.termDocs(terms.term());
            data.add(new AbstractMap.SimpleEntry<>(terms.term().text(), terms.docFreq()));
            builder.append("Term: ").append(terms.term().text()).append("(Freq: ").append(terms.docFreq()).append(")").append("\n");
            while (docuemts.next()) {
                builder.append("\tDocument No.: ").append(docuemts.doc()).append(", Freq: ").append(docuemts.freq()).append("\n");
            }
        }

        Collections.sort(data, new Comparator<Map.Entry<String, Integer>>() {

            @Override
            public int compare(Map.Entry<String, Integer> o1, Map.Entry<String, Integer> o2) {
                return o2.getValue() - o1.getValue();
            }
        });

        builder = new StringBuilder();
        for (Map.Entry<String, Integer> entry : data) {
            Object object = entry.getKey();
            Object object1 = entry.getValue();
            builder.append("Term: ").append(object).append("(Freq: ").append(object1).append(")").append("\n");
        }

        writer.write(builder.toString());
        writer.flush();
        writer.close();
    }
    
    public String getUrl(String path) {
        String url = "";
        url = path.substring(path.lastIndexOf("/") + 1, path.lastIndexOf("_"));
        return url;
    }
    
    public String getFileName(String path) {
        return new File(path).getName();
    }

    public static void main(String... args) throws Exception {
//        int counter = 1;
//        for (Iterator<?> it = ENGLISH_STOP_WORDS.iterator(); it.hasNext();) {
////            String string = ;
//            System.out.println("Counter: " + counter + ", " + it.next());
//            counter++;
//        }
//        List<String> list = new ArrayList<>();
//        BufferedReader reader = new BufferedReader(new FileReader(IndexingUtil.class.getResource("stopwords.en").getPath()));
//        String line;
//        while ((line = reader.readLine()) != null) {
//            if (!list.contains(line.trim())) {
//                list.add(line.trim());
//            }
//        }
//
//        for (String string : list) {
//            System.out.println(string);
//        }
        
        String path = "/home/interactive/Desktop/Computer/Projects/DownloadingDataset/dataset/Law/news.bbc.co.uk_17218.html";
        System.out.println(new IndexingUtil(path, path).getUrl(path));
    }
}
