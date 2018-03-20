/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.dataset.engine;

import java.io.BufferedReader;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author interactive
 */
public class DatasetLog {

    private List<Log> logs;

    public DatasetLog(String path) {
        this.logs = new ArrayList<>();

        try {
            BufferedReader reader = new BufferedReader(new FileReader(path));
            String[] values;
            String line;
            String url;
            String imageUrl;
            String pageName;

            while ((line = reader.readLine()) != null) {
                values = line.split("\t");
                url = values[0];
                imageUrl = values[1];
                pageName = values[2];
                logs.add(new Log(url, imageUrl, pageName));
            }
        } catch (Exception ex) {
            System.err.println(ex);
        }
    }

    public String getImageUrl(String pageName) {
        for (Log log : logs) {
            if (pageName.equalsIgnoreCase(log.pageName)) {
                return log.imageUrl;
            }
        }
        return null;
    }

    public boolean validate(String pageName, String imageUrl) {
        return imageUrl.equalsIgnoreCase(getImageUrl(pageName));
    }

    public void print() {
        for (Log log : logs) {
            System.out.println("URL: " + log.url);
            System.out.println("Image URL: " + log.imageUrl);
            System.out.println("Page Name: " + log.pageName);
            System.out.println("------------------");
        }
    }

    public static class Log {

        private String url;
        private String imageUrl;
        private String pageName;

        public Log(String url, String imageUrl, String pageName) {
            this.url = url;
            this.imageUrl = imageUrl;
            this.pageName = pageName;
        }

        public String getPageName() {
            return pageName;
        }

        public void setPageName(String pageName) {
            this.pageName = pageName;
        }

        public String getUrl() {
            return url;
        }

        public void setUrl(String url) {
            this.url = url;
        }

        public String getImageUrl() {
            return imageUrl;
        }

        public void setImageUrl(String imageUrl) {
            this.imageUrl = imageUrl;
        }
    }

    public static void main(String... args) {
        new DatasetLog("/home/interactive/Desktop/dataset/log.cvs").print();
    }
}
