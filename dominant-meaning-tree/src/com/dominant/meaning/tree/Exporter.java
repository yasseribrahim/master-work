/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.dominant.meaning.tree;

import com.dominant.meaning.tree.node.Node;
import java.io.File;
import java.io.IOException;
import java.util.AbstractMap;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.TransformerFactoryConfigurationError;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import org.w3c.dom.DOMException;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

/**
 *
 * @author interactive
 */
public class Exporter {

    public boolean exporter(Tree<Bag> tree, String path) {
        try {
            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            DocumentBuilder builder = factory.newDocumentBuilder();
            Document document = builder.newDocument();

            Element root = document.createElement(ROOT_TAG);
            document.appendChild(root);

            List<Node<Bag>> childrens;
            Node<Bag> children;
            Element concept, word, documents;
            int counter;
            for (Node<Bag> node : tree.getRoot().getChildrens()) {
                concept = document.createElement(CONCEPT_TAG);
                concept.setAttribute(VALUE_ATTRIBUTE, node.getValue().getWord());
                concept.setAttribute(WEIGHT_ATTRIBUTE, "" + node.getValue().getWeight());

                childrens = node.getChildrens();
                counter = 0;
                while ((counter < childrens.size()) && (counter < MAX_CHILDREN_NUMBER)) {
                    children = childrens.get(counter);
                    word = document.createElement(WORD_TAG);
                    word.setAttribute(VALUE_ATTRIBUTE, children.getValue().getWord());
                    word.setAttribute(WEIGHT_ATTRIBUTE, "" + children.getValue().getWeight());

                    for (Map.Entry<String, Integer> entry : node.getValue().getDocuments()) {
                        documents = document.createElement(DOCUMENT_TAG);
                        documents.setAttribute(NAME_ATTRIBUTE, entry.getKey());
                        documents.setAttribute(FREQUENCY_ATTRIBUTE, "" + entry.getValue());
                        word.appendChild(documents);
                    }
                    concept.appendChild(word);
                    counter++;
                }
                root.appendChild(concept);
            }

            // save document
            TransformerFactory transformerFactory = TransformerFactory.newInstance();
            Transformer transformer = transformerFactory.newTransformer();
            transformer.setOutputProperty(OutputKeys.INDENT, "yes");
            transformer.setOutputProperty("{http://xml.apache.org/xslt}indent-amount", "4");
            DOMSource source = new DOMSource(document);
            StreamResult result = new StreamResult(new File(path));
            transformer.transform(source, result);

            return true;
        } catch (ParserConfigurationException ex) {
            System.err.println(ex);
        } catch (TransformerFactoryConfigurationError ex) {
            System.err.println(ex);
        } catch (DOMException ex) {
            System.err.println(ex);
        } catch (IllegalArgumentException ex) {
            System.err.println(ex);
        } catch (TransformerConfigurationException ex) {
            System.err.println(ex);
        } catch (TransformerException ex) {
            System.err.println(ex);
        }

        return false;
    }

    public boolean exporter(Node<Bag> node, String path) {
        try {
            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            DocumentBuilder builder = factory.newDocumentBuilder();
            Document document = builder.newDocument();

            Element root = document.createElement(ROOT_TAG);
            document.appendChild(root);

            List<Node<Bag>> childrens;
            Node<Bag> children;
            Element concept, word, documents;
            int counter;

            concept = document.createElement(CONCEPT_TAG);
            concept.setAttribute(VALUE_ATTRIBUTE, node.getValue().getWord());
            concept.setAttribute(WEIGHT_ATTRIBUTE, "" + node.getValue().getWeight());

            childrens = node.getChildrens();
            counter = 0;
            while ((counter < childrens.size()) && (counter < MAX_CHILDREN_NUMBER)) {
                children = childrens.get(counter);
                word = document.createElement(WORD_TAG);
                word.setAttribute(VALUE_ATTRIBUTE, children.getValue().getWord());
                word.setAttribute(WEIGHT_ATTRIBUTE, "" + children.getValue().getWeight());

                for (Map.Entry<String, Integer> entry : children.getValue().getDocuments()) {
                    documents = document.createElement(DOCUMENT_TAG);
                    documents.setAttribute(NAME_ATTRIBUTE, entry.getKey());
                    documents.setAttribute(FREQUENCY_ATTRIBUTE, "" + entry.getValue());
                    word.appendChild(documents);
                }
                concept.appendChild(word);
                counter++;
            }
            root.appendChild(concept);

            // save document
            TransformerFactory transformerFactory = TransformerFactory.newInstance();
            Transformer transformer = transformerFactory.newTransformer();
            transformer.setOutputProperty(OutputKeys.INDENT, "yes");
            transformer.setOutputProperty("{http://xml.apache.org/xslt}indent-amount", "4");
            DOMSource source = new DOMSource(document);
            StreamResult result = new StreamResult(new File(path));
            transformer.transform(source, result);

            return true;
        } catch (ParserConfigurationException ex) {
            System.err.println(ex);
        } catch (TransformerFactoryConfigurationError ex) {
            System.err.println(ex);
        } catch (DOMException ex) {
            System.err.println(ex);
        } catch (IllegalArgumentException ex) {
            System.err.println(ex);
        } catch (TransformerConfigurationException ex) {
            System.err.println(ex);
        } catch (TransformerException ex) {
            System.err.println(ex);
        }

        return false;
    }

    public boolean exporter(Document document, Node<Bag> node, String path, boolean save) {
        try {
            Element root = document.createElement(ROOT_TAG);
            document.appendChild(root);

            List<Node<Bag>> childrens;
            Node<Bag> children;
            Element concept, word, documents;
            int counter;

            concept = document.createElement(CONCEPT_TAG);
            concept.setAttribute(VALUE_ATTRIBUTE, node.getValue().getWord());
            concept.setAttribute(WEIGHT_ATTRIBUTE, "" + node.getValue().getWeight());

            childrens = node.getChildrens();
            counter = 0;
            while ((counter < childrens.size()) && (counter < MAX_CHILDREN_NUMBER)) {
                children = childrens.get(counter);
                word = document.createElement(WORD_TAG);
                word.setAttribute(VALUE_ATTRIBUTE, children.getValue().getWord());
                word.setAttribute(WEIGHT_ATTRIBUTE, "" + children.getValue().getWeight());

                for (Map.Entry<String, Integer> entry : node.getValue().getDocuments()) {
                    documents = document.createElement(DOCUMENT_TAG);
                    documents.setAttribute(NAME_ATTRIBUTE, entry.getKey());
                    documents.setAttribute(FREQUENCY_ATTRIBUTE, "" + entry.getValue());
                    word.appendChild(documents);
                }
                concept.appendChild(word);
                counter++;
            }
            root.appendChild(concept);

            if (save) {
                // save document
                TransformerFactory transformerFactory = TransformerFactory.newInstance();
                Transformer transformer = transformerFactory.newTransformer();
                transformer.setOutputProperty(OutputKeys.INDENT, "yes");
                transformer.setOutputProperty("{http://xml.apache.org/xslt}indent-amount", "4");
                DOMSource source = new DOMSource(document);
                StreamResult result = new StreamResult(new File(path));
                transformer.transform(source, result);
            }

            return true;
        } catch (TransformerFactoryConfigurationError ex) {
            System.err.println(ex);
        } catch (DOMException ex) {
            System.err.println(ex);
        } catch (IllegalArgumentException ex) {
            System.err.println(ex);
        } catch (TransformerConfigurationException ex) {
            System.err.println(ex);
        } catch (TransformerException ex) {
            System.err.println(ex);
        }

        return false;
    }

    public Tree<Bag> importer(String path) {
        Tree<Bag> tree = new Tree<Bag>(new Bag(ROOT_TAG, 0), null, new DefaultNodeVisitor<Bag>());
        Node<Bag> conceptNode;
        try {
            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            DocumentBuilder builder = factory.newDocumentBuilder();
            Document document = builder.parse(new File(path));

            Element root = document.getDocumentElement();
            NodeList concepts = root.getElementsByTagName(CONCEPT_TAG);
            NodeList words, documents;
            Element concept, word;
            List<Map.Entry<String, Integer>> documentsList;
            String value, name;
            Bag bag;
            float weight;
            int frequency;

            for (int i = 0; i < concepts.getLength() && i <= MAX_CHILDREN_IMPORT_NUMBER; i++) {
                concept = (Element) concepts.item(i);
                value = concept.getAttribute(VALUE_ATTRIBUTE);
                weight = Float.parseFloat(concept.getAttribute(WEIGHT_ATTRIBUTE));

                conceptNode = tree.getRoot().addChildren(new Bag(value, weight));
                words = concept.getElementsByTagName(WORD_TAG);
                for (int j = 0; j < words.getLength(); j++) {
                    documentsList = new ArrayList<Map.Entry<String, Integer>>();
                    word = (Element) words.item(j);

                    value = word.getAttribute(VALUE_ATTRIBUTE);
                    weight = Float.parseFloat(word.getAttribute(WEIGHT_ATTRIBUTE));

                    documents = word.getElementsByTagName(DOCUMENT_TAG);
                    for (int k = 0; k < documents.getLength(); k++) {
                        name = ((Element) documents.item(k)).getAttribute(NAME_ATTRIBUTE);
                        frequency = Integer.parseInt(((Element) documents.item(k)).getAttribute(FREQUENCY_ATTRIBUTE));
                        documentsList.add(new AbstractMap.SimpleEntry<String, Integer>(name, frequency));
                    }

                    bag = new Bag(value, weight);
                    bag.setDocuments(documentsList);
                    conceptNode.addChildren(bag);
                }
            }
        } catch (ParserConfigurationException ex) {
            System.err.println(ex);
        } catch (SAXException ex) {
            System.err.println(ex);
        } catch (IOException ex) {
            System.err.println(ex);
        }

        return tree;
    }

    public static final int MAX_CHILDREN_NUMBER = 100;
    public static final int MAX_CHILDREN_IMPORT_NUMBER = 30;
    public static final String ROOT_TAG = "root";
    public static final String CONCEPT_TAG = "concept";
    public static final String WORD_TAG = "word";
    public static final String DOCUMENT_TAG = "document";
    public static final String VALUE_ATTRIBUTE = "value";
    public static final String WEIGHT_ATTRIBUTE = "weight";
    public static final String NAME_ATTRIBUTE = "name";
    public static final String FREQUENCY_ATTRIBUTE = "frequency";
}
