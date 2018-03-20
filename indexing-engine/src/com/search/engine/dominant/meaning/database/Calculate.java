/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.search.engine.dominant.meaning.database;

import com.dataset.engine.DatasetManager;
import com.dataset.engine.FileManager;
import com.dataset.engine.Naming;
import com.dominant.meaning.tree.Bag;
import com.dominant.meaning.tree.Tree;
import com.search.engine.dominant.meaning.DominantMeaningDatabaseManager;
import static com.search.engine.dominant.meaning.DominantMeaningDatabaseManager.getManager;
import com.search.engine.indexing.CustomXmlIndexing;
import com.search.engine.utilies.IndexingUtil;
import static com.search.engine.utilies.IndexingUtil.ENGLISH_STOP_WORDS;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.index.IndexReader;
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
public class Calculate {

    private static final DataAccessMgr MGR = DataAccessMgr.getCurrentInstance();

    public static void main(String[] args) throws Exception {
//        fill();
//        check();
//        repair();
        results();
    }

    public static void results() throws Exception {
        String workingDirectory = "/home/interactive/Desktop/output" + FileManager.FILE_SEPARATOR;
        String workingDirectory2 = "/home/interactive/Desktop/output_1472244882142" + /*System.currentTimeMillis() +*/ FileManager.FILE_SEPARATOR;
        String datasetDirectory = "/home/interactive/Desktop/Computer/Java Jars/Maining/iaprtc12/annotations_complete_eng";
        DominantMeaningDatabaseManager manager = getManager(workingDirectory2, datasetDirectory);
        CustomXmlIndexing indexing = CustomXmlIndexing.getIndexing(workingDirectory, datasetDirectory);
        IndexingUtil util = IndexingUtil.getInstance(workingDirectory, datasetDirectory);

//        manager.buildModel(0.5f);
//        indexing.buildIndex();
        List<Concept> concepts = MGR.getConcepts();
        IndexReader reader;
        IndexSearcher searcher;
        QueryParser parser = new QueryParser(Version.LUCENE_32, Naming.FIELD_DESCRIPTION, new StandardAnalyzer(Version.LUCENE_32, ENGLISH_STOP_WORDS));
        Query query;
        TopDocs topDocuments;
        ScoreDoc[] documents;
        Document document;
        Tree<Bag> tree;
        List<String> paths;
        String queryString, path;
        Result result;
        String conceptValue;
        String conceptName;
        int size;
        int expected;
        int retrieved;
        int hit;
        float precision;
        float recall;
        float f1;

        for (Concept concept : concepts) {
            conceptValue = concept.getValue();
            conceptName = concept.getName();
            reader = util.getIndexReader(concept.getValue());
            searcher = new IndexSearcher(reader);
            tree = manager.getExporter().importer(workingDirectory2 + "/output/" + concept.getValue() + ".xml");

            for (int i = 0; i <= 100; i += 5) {
                try {
                    if (i == 0) {
                        queryString = conceptName;
                    } else {
                        queryString = tree.getRoot().getChildrens().get(0).getChildrensAsString(" ", i);
                    }
                    query = parser.parse(queryString);
                    paths = tree.getRoot().getChildrens().get(0).getDocuments(i);
//                    paths = MGR.documents(concept.getId(), queryString.split(" "));

                    topDocuments = searcher.search(query, 100000);
                    documents = topDocuments.scoreDocs;
                    hit = 0;
                    for (ScoreDoc doc : documents) {
                        document = reader.document(doc.doc);
                        path = document.get(Naming.FIELD_PATH);
                        if (paths.contains(path)) {
                            hit++;
                        }
                    }
                    size = i;
                    expected = paths.size();
                    retrieved = documents.length;
                    precision = (float) (1.0 * hit) / retrieved;
                    recall = (float) (1.0 * hit) / expected;
                    f1 = (float) ((2.0 * recall * precision) / (recall + precision));

                    result = new Result(conceptValue, conceptName, size, hit, expected, retrieved, precision * 100, recall * 100, f1 * 100);
                    MGR.saveResult(result);
                    System.out.println("Saved: " + result);
                } catch (Exception ex) {
                }

            }
        }
    }

    public static void repair() {

        List<Concept> concepts = MGR.getConcepts();
        List<Frequent> frequents;
        for (Concept concept : concepts) {
            frequents = MGR.getFrequents(concept.getName());
            if (!frequents.isEmpty()) {
                concept.setName(frequents.get(0).getTerm());
                MGR.updateConcept(concept);
            }
        }
    }

    public static void check() {
        List<Concept> concepts = MGR.getConcepts();
        List<Frequent> frequents;

        int counter;
        Map<String, Integer> words = new HashMap<>();
        Integer value;
        for (Concept concept : concepts) {
            frequents = MGR.getFrequents(concept.getName());
            System.out.println(concept);

            counter = 0;
            for (Frequent frequent : frequents) {
                System.out.println("\t" + frequent);
                counter++;
                value = words.get(frequent.getTerm());
                if (value == null) {
                    value = 0;
                }
                words.put(frequent.getTerm(), ++value);
                if (counter > 100) {
                    break;
                }
            }
        }
        for (Map.Entry<String, Integer> entry : words.entrySet()) {
            String key = entry.getKey();
            Integer value1 = entry.getValue();
            if (value1 > 20) {
                System.out.println(key + ", " + value1);
            }
        }
    }

    public static void fill() throws Exception {
        String workingDirectory = "/home/interactive/Desktop/output" + FileManager.FILE_SEPARATOR;
        String datasetDirectory = "/home/interactive/Desktop/Computer/Java Jars/Maining/iaprtc12/annotations_complete_eng";
        DatasetManager dataset = DatasetManager.getManager(datasetDirectory, workingDirectory);
        CustomXmlIndexing indexing = CustomXmlIndexing.getIndexing(workingDirectory, datasetDirectory);
        IndexingUtil util = IndexingUtil.getInstance(workingDirectory, datasetDirectory);
        IndexReader reader;
        Document document;
        TermEnum terms;
        TermDocs documents;
        Concept concept;
        ConceptDetails details;

        indexing.buildIndex();
        for (DatasetManager.Data data : dataset.getDatas()) {
            try {
                reader = util.getIndexReader(data.getName());
                concept = new Concept(null, data.getName(), data.getName());
                concept = MGR.saveConcept(concept);
                terms = reader.terms();
                while (terms.next()) {
                    documents = reader.termDocs(terms.term());

                    while (documents.next()) {
                        document = reader.document(documents.doc());
                        details = new ConceptDetails(null, terms.term().text(), document.get(Naming.FIELD_PATH), documents.freq());
                        details.setConceptId(concept);
                        MGR.saveConceptDetails(details);
                        System.out.println("Saved: " + details);
                    }
                }
            } catch (IOException ex) {
                System.err.println(ex);
            }

        }
    }
}
