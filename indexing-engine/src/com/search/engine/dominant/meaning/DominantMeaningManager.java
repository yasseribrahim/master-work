package com.search.engine.dominant.meaning;

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
public class DominantMeaningManager {

    private final DatasetManager dataset;
    private final CustomXmlIndexing indexing;
    private final IndexingUtil util;
    private final Exporter exporter;
    private IndexReader reader;
    private Tree<Bag> tree;

    public static DominantMeaningManager getManager(String workingDirectory, String datasetDirectory) {
        return new DominantMeaningManager(workingDirectory, datasetDirectory);
    }

    private DominantMeaningManager(String workingDirectory, String datasetDirectory) {
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

    public int calculateMaximumFrequenceConcept(String name) throws IOException {
        TermDocs docs = reader.termDocs(new Term(Naming.FIELD_CONTENT, name));

        int maximum = 0;
        while (docs.next()) {
            if (docs.freq() > maximum) {
                maximum = docs.freq();
            }
        }

        return maximum;
    }

    public int calculateMaximumFrequenceTerm() throws Exception {
        TermEnum terms = reader.terms();
        TermDocs docuemts;

        int maximum = 0;
        while (terms.next()) {
            docuemts = reader.termDocs(terms.term());

            while (docuemts.next()) {
                if (docuemts.freq() > maximum) {
                    maximum = docuemts.freq();
                }
            }
        }

        return maximum;
    }

    public int chooseMaximumFrequenceConcept(String name) throws Exception {
        int maximum = calculateMaximumFrequenceConcept(name);
        int maximumFrequenceTerm = calculateMaximumFrequenceTerm();

        if (maximum < maximumFrequenceTerm) {
            maximum = maximumFrequenceTerm;
        }

        return maximum;
    }

    public Node<Bag> calculate(Node<Bag> root, String concept) throws Exception {
        TermEnum terms = reader.terms();
        TermDocs documents;
        int numberDocuments = reader.numDocs();
        int maximumFrequence = chooseMaximumFrequenceConcept(concept);

        Node<Bag> node = root.addChildren(new Bag(concept, 0));
        double weight;
        double summation;
        List<Map.Entry<String, Integer>> documentsList;
        Bag bag;
        String path, url;
        while (terms.next()) {
            documents = reader.termDocs(terms.term());
            documentsList = new ArrayList<>();

            summation = 0.0d;
            while (documents.next()) {
                path = reader.document(documents.doc()).get(Naming.FIELD_PATH);
                url = util.getUrl(path);

                documentsList.add(new AbstractMap.SimpleEntry<>(url, (int) documents.freq()));
                summation += ((documents.freq() * 1.0) / maximumFrequence);
            }

            weight = (1.0 / numberDocuments) * summation;
            if (concept.equalsIgnoreCase(terms.term().text())) {
                node.getValue().setWeight((float) weight);
            }

            bag = new Bag(terms.term().text(), (float) weight);
            bag.setDocuments(documentsList);
            node.addChildren(bag);
        }
        node.repaire();
        return node;
    }

    public void calculateAndExport(String concept) throws Exception {
        TermEnum terms = reader.terms();
        TermDocs documents;
        int numberDocuments = reader.numDocs();
        int maximumFrequence = chooseMaximumFrequenceConcept(concept);

        Node<Bag> node = new Node<>(new Bag(concept, 0), null, new Comparator<Bag>() {

            @Override
            public int compare(Bag t1, Bag t2) {
                return (t1.getWeight() <= t2.getWeight()) ? 1 : -1;
            }
        });
        double weight;
        double summation;
        List<Map.Entry<String, Integer>> documentsList;
        Bag bag;
        String path;
        while (terms.next()) {
            documents = reader.termDocs(terms.term());
            documentsList = new ArrayList<>();

            summation = 0.0d;
            while (documents.next()) {
                path = reader.document(documents.doc()).get(Naming.FIELD_PATH);

                documentsList.add(new AbstractMap.SimpleEntry<>(path, (int) documents.freq()));
                summation += ((documents.freq() * 1.0) / maximumFrequence);
            }

            weight = (1.0 / numberDocuments) * summation;
            if (concept.equalsIgnoreCase(terms.term().text())) {
                node.getValue().setWeight((float) weight);
            }

            bag = new Bag(terms.term().text(), (float) weight);
            bag.setDocuments(documentsList);
            node.addChildren(bag);
        }
        node.repaire();

        exporter.exporter(node, dataset.getOutputDirectory() + FileManager.FILE_SEPARATOR /*+ node.getChildrens().get(0).getValue().getWord() + "_"*/ + concept + ".xml");
    }

    public Tree<Bag> calculate() throws Exception {
        tree = new Tree<>(new Bag("root", 0), null, new DefaultNodeVisitor<>());

        for (DatasetManager.Data data : dataset.getDatas()) {
            setReader(data.getName());
            calculate(tree.getRoot(), data.getName());
        }
        return tree;
    }

    public void calculateAndExport() throws Exception {
        for (DatasetManager.Data data : dataset.getDatas()) {
            setReader(data.getName());
            calculateAndExport(data.getName());
        }
    }

    public static void main(String... args) throws Exception {
        String workingDir = FileManager.DEDFAULT_WORKING_DIRECTORY + FileManager.FILE_SEPARATOR + "output" + FileManager.FILE_SEPARATOR;
        String datasetDir = FileManager.DEDFAULT_WORKING_DIRECTORY + FileManager.FILE_SEPARATOR + "dataset";
        DominantMeaningManager manager = getManager(workingDir, datasetDir);
        manager.buildModel();
        normalize(workingDir, datasetDir);
        getResults(workingDir, datasetDir);
//        getResults2(workingDir, datasetDir);
//        validateTree(workingDir, datasetDir);
    }

    public static void validateTree(String workingDir, String datasetDir) throws Exception {
        DominantMeaningManager manager = getManager(workingDir, datasetDir);
        Exporter exporter = new Exporter();
        Tree<Bag> tree, tree1;
        StringBuilder builder = new StringBuilder();
        IndexReader reader;
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
        DominantMeaningManager manager = getManager(workingDir, datasetDir);
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
        List<Map.Entry<ScoreDoc, Float>> list;
        String queryString, path;
        StringBuilder out = new StringBuilder();
        Map.Entry<Float, Float> pair;
        float threshold;
        float[] range;
        int counter, hit;

        for (DatasetManager.Data data : manager.dataset.getDatas()) {
            tree = manager.exporter.importer(workingDir + "/output/" + data.getName() + ".xml");

            out.append(data.getName()).append("\n");
            out.append("----------------------------------------------\n");
            out.append("\n");
            for (int i = 5; i <= 100; i += 5) {
                try {
                    queryString = tree.getRoot().getChildrens().get(0).getChildrensAsString(" ", i);
                    query = parser.parse(queryString);
                    paths = tree.getRoot().getChildrens().get(0).getDocuments(i);

                    topDocuments = searcher.search(query, 100000);
                    documents = topDocuments.scoreDocs;
                    hit = 0;
                    counter = 0;
                    range = range(documents);
                    list = normalize(documents, range);
                    pair = threshold(list, paths, reader);
                    threshold = 0.0f;//Math.max(pair.getKey(), pair.getValue());
                    for (Map.Entry<ScoreDoc, Float> entry : list) {
                        document = reader.document(entry.getKey().doc);
                        path = document.get(Naming.FIELD_PATH);
                        if (entry.getValue() >= threshold) {
                            counter++;
                            if (paths.contains(path)) {
                                hit++;
                            }
                        }
                    }
                    out.append("Query Size: ").append(queryString.split(" ").length).append(", Hit: ").append(hit).append(", Expected Result: ").append(data.getFiles().length).append(", Retrieved Result: ").append(counter).append("\n");
                    out.append("____________________________________________________________\n");
                } catch (Exception ex) {
                }

            }
            out.append("_____________________________\n");
        }
        System.out.println(out.toString());
    }

    public static void getResults2(String workingDir, String datasetDir) throws Exception {
        DominantMeaningManager manager = getManager(workingDir, datasetDir);
        manager.buildModel();
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

                out.append(i).append(",").append(hit).append(",").append(paths.size()).append(",").append(documents.length).append(",").append(result.get("bus")).append(",").append(result.get("car")).append(",").append(result.get("cat")).append(",").append(result.get("chair")).append(",").append(result.get("dog")).append("\n");
            }
            out.append("_____________________________\n");
        }
        System.out.println(out.toString());
    }

    public static void normalize(String workingDir, String datasetDir) throws Exception {
        DominantMeaningManager manager = getManager(workingDir, datasetDir);
//        manager.buildModel();
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
        float max1, min1, max2, min2, minmummal = 0;
        List<Float> list1, list2;
        List<Float> inside, outside;

        for (DatasetManager.Data data : manager.dataset.getDatas()) {
            tree = manager.exporter.importer(workingDir + "/output/" + data.getName() + ".xml");

            out.append(data.getName()).append("\n");
            out.append("----------------------------------------------\n");
            out.append("\n");
            for (int i = 5; i <= 100; i += 5) {
                try {
                    queryString = tree.getRoot().getChildrens().get(0).getChildrensAsString(" ", i);
                    query = parser.parse(queryString);
                    paths = tree.getRoot().getChildrens().get(0).getDocuments(i);

                    topDocuments = searcher.search(query, 100000);
                    documents = topDocuments.scoreDocs;
                    max1 = Float.MIN_VALUE;
                    min1 = Float.MAX_VALUE;
                    max2 = Float.MIN_VALUE;
                    min2 = Float.MAX_VALUE;
                    minmummal = Float.MIN_VALUE;
                    list1 = new ArrayList<>();
                    list2 = new ArrayList<>();
                    inside = new ArrayList<>();
                    outside = new ArrayList<>();
                    for (ScoreDoc score : documents) {
                        document = reader.document(score.doc);
                        path = document.get(Naming.FIELD_PATH);
                        if (paths.contains(path)) {
                            list1.add(score.score);
                            if (max1 < score.score) {
                                max1 = score.score;
                            }
                            if (min1 > score.score) {
                                min1 = score.score;
                            }
                        } else {
                            list2.add(score.score);
                            if (max2 < score.score) {
                                max2 = score.score;
                            }
                            if (min2 > score.score) {
                                min2 = score.score;
                            }
                        }
                    }

                    // to normalize data
                    for (int j = 0; j < list1.size(); j++) {
                        inside.add((list1.get(j) - min1) / (max1 - min1));
                    }
                    for (int j = 0; j < list2.size(); j++) {
                        outside.add((list2.get(j) - min2) / (max2 - min2));
                    }

                    int index = 0;
                    float value = Float.MAX_VALUE, minVal = Float.MAX_VALUE;
                    while (index < inside.size() - 1 && index < outside.size() - 1) {
                        if (value > (inside.get(index) - outside.get(index))) {
                            value = (inside.get(index) - outside.get(index));
                            minVal = inside.get(index);
                            if (minmummal < minVal) {
                                minmummal = minVal;
                            }
                        }
                        index++;
                    }
                    out.append(i).append("\n");
                    out.append(minVal).append("\n");
                    out.append("____________________________________________________________________").append("\n");
                } catch (Exception ex) {
                }
            }
            break;
        }
        System.out.println(out.toString());
        System.out.println("Thraed Should: " + minmummal);
    }

    public static List<Map.Entry<ScoreDoc, Float>> normalize(ScoreDoc[] documents, float[] range) throws Exception {
        float max, min;
        List<Map.Entry<ScoreDoc, Float>> list = new ArrayList<>();
        max = range[0];
        min = range[1];
        for (ScoreDoc score : documents) {
            list.add(new AbstractMap.SimpleEntry<>(score, (score.score - min) / (max - min)));
        }

        return list;
    }

    public static float[] range(ScoreDoc[] documents) throws Exception {
        float max, min;

        max = Float.MIN_VALUE;
        min = Float.MAX_VALUE;
        for (ScoreDoc score : documents) {
            if (max < score.score) {
                max = score.score;
            }
            if (min > score.score) {
                min = score.score;
            }
        }

        return new float[]{max, min};
    }

    public static Map.Entry<Float, Float> threshold(List<Map.Entry<ScoreDoc, Float>> list, List<String> paths, IndexReader reader) throws Exception {
        List<Map.Entry<ScoreDoc, Float>> list1 = new ArrayList<>(), list2 = new ArrayList<>();
        Map.Entry<Float, Float> threshold = null;
        Document document;
        String path;
        float min = Float.MAX_VALUE, diff;
        for (Map.Entry<ScoreDoc, Float> entry : list) {
            document = reader.document(entry.getKey().doc);
            path = document.get(Naming.FIELD_PATH);
            if (paths.contains(path)) {
                list1.add(entry);
            } else {
                list2.add(entry);
            }
        }
        
        for (Map.Entry<ScoreDoc, Float> entry1 : list1) {
            for (Map.Entry<ScoreDoc, Float> entry2 : list2) {
                diff = Math.abs(entry1.getValue() - entry2.getValue());
                if (min > diff) {
                    min = diff;
                    threshold = new AbstractMap.SimpleEntry<>(entry1.getValue(), entry2.getValue());
                }
            }
        }
        
        return threshold;
    }
}
