package com.search.engine.tfidf;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
import com.dataset.engine.DatasetManager;
import com.dataset.engine.FileManager;
import com.dataset.engine.Naming;
import com.dominant.meaning.tree.Bag;
import com.dominant.meaning.tree.DefaultNodeVisitor;
import com.dominant.meaning.tree.Exporter;
import com.dominant.meaning.tree.Tree;
import com.dominant.meaning.tree.node.Node;
import com.search.engine.indexing.CustomXmlIndexing;
import com.search.engine.utilies.IndexingUtil;
import static com.search.engine.utilies.IndexingUtil.ENGLISH_STOP_WORDS;
import java.io.IOException;
import java.util.AbstractMap;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.index.Term;
import org.apache.lucene.index.TermDocs;
import org.apache.lucene.index.TermEnum;
import org.apache.lucene.queryParser.QueryParser;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.TopDocs;
import org.apache.lucene.util.Version;

/**
 *
 * @author interactive
 */
public class TFIDFManagerXmlDocuments {

    private final DatasetManager dataset;
    private final CustomXmlIndexing indexing;
    private final IndexingUtil util;
    private final Exporter exporter;
    private IndexReader reader;
    private Tree<Bag> tree;

    public static TFIDFManagerXmlDocuments getManager(String workingDirectory, String datasetDirectory) {
        return new TFIDFManagerXmlDocuments(workingDirectory, datasetDirectory);
    }

    private TFIDFManagerXmlDocuments(String workingDirectory, String datasetDirectory) {
        this.dataset = DatasetManager.getManager(datasetDirectory, workingDirectory);
        indexing = CustomXmlIndexing.getIndexing(workingDirectory, datasetDirectory);
        this.util = IndexingUtil.getInstance(workingDirectory, datasetDirectory);
        this.exporter = new Exporter();
    }

    public void setReader(String name) {
        try {
            this.reader = util.getIndexReader(name);
        } catch (IOException ex) {
            System.err.println(ex);
        }
    }

    public final boolean buildModel() {
        try {
            indexing.buildIndex();
            ((CustomXmlIndexing) indexing).buildPublicIndex();
            calculateAndExport();
//            exporter.exporter(tree, dataset.getTreeFile().getAbsolutePath());
        } catch (Exception ex) {
            System.err.println(ex);
            return false;
        }
        return true;
    }

    public Tree<Bag> getTree() {
        return tree;
    }

    public int calculateFrequenceTerm(Term term) throws IOException {
        TermDocs docs = reader.termDocs(term);

        int total = 0;
        while (docs.next()) {
            total += docs.freq();
        }

        return total;
    }

    public int calculateDocumentsTerm(Term term) throws IOException {
        TermDocs docs = reader.termDocs(term);

        int total = 0;
        while (docs.next()) {
            total++;
        }

        return total;
    }

    public List<Map.Entry<String, Integer>> calculateDocumentListTerm(Term term) throws IOException {
        TermDocs documents = reader.termDocs(term);
        List<Map.Entry<String, Integer>> documentsList = new ArrayList<>();

        String path;
        while (documents.next()) {
            path = reader.document(documents.doc()).get(Naming.FIELD_PATH);
            documentsList.add(new AbstractMap.SimpleEntry<>(path, (int) documents.freq()));
        }

        return documentsList;
    }

    public int calculateTotalTerms() throws Exception {
        TermEnum terms = reader.terms();

        int total = 0;
        while (terms.next()) {
            total++;
        }

        return total;
    }

    public Node<Bag> calculate(Node<Bag> root, String concept) throws Exception {
        TermEnum terms = reader.terms();
        int numberDocuments = reader.numDocs();
        int totalTerms = calculateTotalTerms();
        int numberDocumentsTerm, frequenceTerm;
        float tf, idf;

        Node<Bag> node = root.addChildren(new Bag(concept, 0));
        double weight, large = Double.MIN_VALUE;
        while (terms.next()) {
            numberDocumentsTerm = calculateDocumentsTerm(terms.term());
            frequenceTerm = calculateFrequenceTerm(terms.term());
            tf = (float) ((frequenceTerm * 1.0) / totalTerms);
            idf = (float) Math.log10((numberDocuments / 1.0) / numberDocumentsTerm);
            weight = tf * idf;
            if (large < weight) {
                large = weight;
                node.getValue().setWeight((float) weight);
                node.getValue().setWord(terms.term().text());
            }
            node.addChildren(new Bag(terms.term().text(), (float) weight));
        }

        return node;
    }

    public void calculateAndExport() throws Exception {
        for (DatasetManager.Data data : dataset.getDatas()) {
            setReader(data.getName());
            calculateAndExport(data.getName());
        }
    }

    public void calculateAndExport(String concept) throws Exception {
        TermEnum terms = reader.terms();
        int numberDocuments = reader.numDocs();
        int totalTerms = calculateTotalTerms();
        int numberDocumentsTerm, frequenceTerm;
        float tf, idf;

        Node<Bag> node = new Node<>(new Bag(concept, 0), null, new Comparator<Bag>() {

            @Override
            public int compare(Bag t1, Bag t2) {
                return (t1.getWeight() <= t2.getWeight()) ? 1 : -1;
            }
        });

        double weight, large = Double.MIN_VALUE;
        Bag bag;
        while (terms.next()) {
            numberDocumentsTerm = calculateDocumentsTerm(terms.term());
            frequenceTerm = calculateFrequenceTerm(terms.term());
            tf = (float) ((frequenceTerm * 1.0) / totalTerms);
            idf = (float) Math.log10((numberDocuments / 1.0) / numberDocumentsTerm);
            weight = tf * idf;
            if (large < weight) {
                large = weight;
//                concept = terms.term().text();
                node.getValue().setWeight((float) weight);
                node.getValue().setWord(terms.term().text());
            }

            bag = new Bag(terms.term().text(), (float) weight);
            bag.setDocuments(calculateDocumentListTerm(terms.term()));
            node.addChildren(bag);
        }

        exporter.exporter(node, dataset.getOutputDirectory() + FileManager.FILE_SEPARATOR + concept + /*"_" + System.currentTimeMillis() + */ ".xml");
    }

    public Tree<Bag> calculate() throws Exception {
        tree = new Tree<>(new Bag("root", 0), null, new DefaultNodeVisitor<>());

        for (DatasetManager.Data data : dataset.getDatas()) {
            setReader(data.getName());
            calculate(tree.getRoot(), data.getName());
        }
        return tree;
    }

    public static void main(String... args) throws Exception {
        String workingDir = "/home/interactive/Desktop/Master Work/Working Dir" + FileManager.FILE_SEPARATOR;
        String datasetDir = "/home/interactive/Desktop/Computer/Java Jars/Maining/iaprtc12/annotations_complete_eng";
        getResults(workingDir, datasetDir);
//        getResults2(workingDir, datasetDir);
//        validateTree(workingDir, datasetDir);
    }

    public static void validateTree(String workingDir, String datasetDir) throws Exception {
        TFIDFManagerXmlDocuments manager = getManager(workingDir, datasetDir);
        Exporter exporter = new Exporter();
        Tree<Bag> tree, tree1;
        StringBuilder builder = new StringBuilder();
        String[] words, words1;
        int counter;
        for (DatasetManager.Data data : manager.dataset.getDatas()) {
            tree = exporter.importer(workingDir + "/output/" + data.getName() + ".xml");
            words = tree.getRoot().getChildrens().get(0).getChildrensAsString(" ", 100).split(" ");
            builder.append(data.getName()).append("\n");
            for (DatasetManager.Data data1 : manager.dataset.getDatas()) {
                if (!data.getName().equalsIgnoreCase(data1.getName())) {
                    counter = 0;
                    tree1 = exporter.importer(workingDir + "/output/" + data1.getName() + ".xml");
                    words1 = tree1.getRoot().getChildrens().get(0).getChildrensAsString(" ", 100).split(" ");

                    for (int i = 0; i < words.length; i++) {
                        for (int j = 0; j < words1.length; j++) {
                            if (words[i].equalsIgnoreCase(words1[j])) {
                                counter++;
                                break;
                            }
                        }
                    }
                    builder.append(data1.getName()).append(": ").append(counter).append("\n");
                }
            }
        }
        System.out.println(builder.toString());
    }

    public static void getResults(String workingDir, String datasetDir) throws Exception {
        TFIDFManagerXmlDocuments manager = getManager(workingDir, datasetDir);
        manager.buildModel();
        IndexReader reader = manager.util.getIndexReader();
        IndexSearcher searcher = new IndexSearcher(reader);
        QueryParser parser = new QueryParser(Version.LUCENE_32, Naming.FIELD_DESCRIPTION, new StandardAnalyzer(Version.LUCENE_32, ENGLISH_STOP_WORDS));
        Query query;
        TopDocs topDocuments;
        ScoreDoc[] documents;
        Document document;
        Tree<Bag> tree;
        List<String> paths;
        String queryString, path;
        StringBuilder out = new StringBuilder();
        int hit;

        for (DatasetManager.Data data : manager.dataset.getDatas()) {
            try {
                tree = manager.exporter.importer(workingDir + "/output/" + data.getName() + ".xml");

                out.append(tree.getRoot().getChildrens().get(0).getValue().getWord()).append("\n");
                for (int i = 5; i <= 100; i += 5) {
                    queryString = tree.getRoot().getChildrens().get(0).getChildrensAsString(" ", i);
//                    System.out.println("Current Query: " + data.getName() + ": " + queryString);
                    query = parser.parse(queryString);
                    paths = tree.getRoot().getChildrens().get(0).getDocuments(i);

                    topDocuments = searcher.search(query, 100000);
                    documents = topDocuments.scoreDocs;
                    hit = 0;
                    for (ScoreDoc score : documents) {
                        document = reader.document(score.doc);
                        path = document.get(Naming.FIELD_PATH);
                        if (paths.contains(path)) {
                            hit++;
                        }
                    }

                    out.append("Query Size: ").append(i).append(", Hit: ").append(hit).append(", Expected Result: ").append(data.getFiles().length).append(", Retrieved Result: ").append(documents.length).append("\n");
                }
                out.append("_____________________________\n");
            } catch (Exception ex) {
                System.err.println(ex);
            }
        }
        System.out.println(out.toString());
    }

    public static void getResults2(String workingDir, String datasetDir) throws Exception {
        TFIDFManagerXmlDocuments manager = getManager(workingDir, datasetDir);
//        manager.buildModel();
        IndexReader reader = manager.util.getIndexReader();
        IndexSearcher searcher = new IndexSearcher(reader);
        QueryParser parser = new QueryParser(Version.LUCENE_32, Naming.FIELD_CONTENT, new StandardAnalyzer(Version.LUCENE_32, ENGLISH_STOP_WORDS));
        Query query;
        TopDocs topDocuments;
        ScoreDoc[] documents;
        Document document;
        Tree<Bag> tree;
        List<String> paths;
        String queryString, path;
        StringBuilder out = new StringBuilder();
        int hit;

        Map<String, Integer> result;
        for (DatasetManager.Data data : manager.dataset.getDatas()) {
            tree = manager.exporter.importer(workingDir + "/output/" + data.getName() + ".xml");

            out.append(data.getName()).append("\n");
            for (int i = 5; i <= 100; i += 5) {
                queryString = tree.getRoot().getChildrens().get(0).getChildrensAsString(" ", i);
                query = parser.parse(queryString);
                paths = tree.getRoot().getChildrens().get(0).getDocuments(i);

                topDocuments = searcher.search(query, 100000);
                documents = topDocuments.scoreDocs;
                result = new HashMap<>();
                hit = 0;
                for (ScoreDoc score : documents) {
                    document = reader.document(score.doc);
                    path = document.get(Naming.FIELD_PATH);
                    if (paths.contains(path)) {
                        hit++;
                    }
                    if (result.containsKey(document.get(Naming.FIELD_CATEGORY))) {
                        result.put(document.get(Naming.FIELD_CATEGORY), result.get(document.get(Naming.FIELD_CATEGORY)) + 1);
                    } else {
                        result.put(document.get(Naming.FIELD_CATEGORY), 1);
                    }
                }

                out.append(i).append(",").append(hit).append(",").append(paths.size()).append(",").append(documents.length).append(",").append(result.get("art")).append(",").append(result.get("medicine")).append(",").append(result.get("business")).append(",").append(result.get("education")).append("\n");
            }
            out.append("_____________________________\n");
        }
        System.out.println(out.toString());
    }
}
