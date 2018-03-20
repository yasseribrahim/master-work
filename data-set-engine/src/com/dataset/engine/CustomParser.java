/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.dataset.engine;

import java.io.File;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

/**
 *
 * @author interactive
 */
public class CustomParser {

    private static final CustomParser PARSER = new CustomParser();

    public static CustomParser getInstance() {
        return PARSER;
    }

    private CustomParser() {

    }

    public Data parse(String file) throws Exception {
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        DocumentBuilder builder = factory.newDocumentBuilder();
        org.w3c.dom.Document document = builder.parse(new File(file));
        document.getDocumentElement().normalize();

        String title = cleanup(document.getElementsByTagName(Naming.FIELD_TITLE.toUpperCase()).item(0).getTextContent());
        String description = cleanup(document.getElementsByTagName(Naming.FIELD_DESCRIPTION.toUpperCase()).item(0).getTextContent());
        String notes = cleanup(document.getElementsByTagName(Naming.FIELD_NOTES.toUpperCase()).item(0).getTextContent());
        String location = cleanup(document.getElementsByTagName(Naming.FIELD_LOCATION.toUpperCase()).item(0).getTextContent());
        String image = document.getElementsByTagName(Naming.FIELD_IMAGE.toUpperCase()).item(0).getTextContent();

        return new Data(title, description, notes, location, image);
    }

    public String cleanup(String text) {
        text = text.replaceAll("(\\s)+", " ").trim(); // cleanup whitespace
        text = text.replaceAll("[0-9]", "").trim(); // cleanup numeric digit
        return text;
    }

    public class Data {

        private String title;
        private String description;
        private String notes;
        private String location;
        private String image;

        public Data(String title, String description, String notes, String location, String image) {
            this.title = title;
            this.description = description;
            this.notes = notes;
            this.location = location;
            this.image = image;
        }

        public String getTitle() {
            return title;
        }

        public void setTitle(String title) {
            this.title = title;
        }

        public String getDescription() {
            return description;
        }

        public void setDescription(String description) {
            this.description = description;
        }

        public String getNotes() {
            return notes;
        }

        public void setNotes(String notes) {
            this.notes = notes;
        }

        public String getLocation() {
            return location;
        }

        public void setLocation(String location) {
            this.location = location;
        }

        public String getImage() {
            return image;
        }

        public void setImage(String image) {
            this.image = image;
        }

        @Override
        public String toString() {
            StringBuilder builder = new StringBuilder();
            builder.append(title).append(" ");
            builder.append(description).append(" ");
            builder.append(notes).append(" ");
            builder.append(location);
            return builder.toString();
        }
    }
}
