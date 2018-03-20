/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.dataset.engine;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileFilter;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author interactive
 */
public class DatasetManager extends FileManager {

    private DatasetManager(String datasetDirectory, String workingDirectory) {
        super(workingDirectory, datasetDirectory);
    }

    public static DatasetManager getManager(String datasetDirectory, String workingDirectory) {
        return new DatasetManager(datasetDirectory, workingDirectory);
    }

    public List<Data> getDatas() {
        File[] files = getDatasetDirectories();
        List<Data> data = new ArrayList<>();
        for (File file : files) {
            data.add(new Data(file.getName().toLowerCase(), getFiles(file)));
        }
        return data;
    }

    public List<Data> getDatas(FileFilter filter) {
        File[] files = getDatasetDirectories();
        List<Data> data = new ArrayList<>();
        for (File file : files) {
            data.add(new Data(file.getName().toLowerCase(), getFiles(file, filter)));
        }
        return data;
    }

    public List<CharSequence> getContent(String... paths) {
        List<CharSequence> contents = new ArrayList<>();
        for (String path : paths) {
            contents.add(getContent(path));
        }
        return contents;
    }

    public List<CharSequence> getContent(File... files) {
        List<CharSequence> contents = new ArrayList<>();
        for (File file : files) {
            contents.add(getContent(file));
        }
        return contents;
    }

    public CharSequence getContent(String path) {
        return getContent(new File(path));
    }

    public CharSequence getContent(File file) {
        StringBuilder builder = new StringBuilder();
        try {
            BufferedReader reader = new BufferedReader(new InputStreamReader(new FileInputStream(file)));
            String line;
            while ((line = reader.readLine()) != null) {
                builder.append(line);
                builder.append('\n');
            }
        } catch (IOException ex) {
            System.err.println(ex);
        }

        return builder;
    }

    public InputStream getInputStream(File file) {
        try {
            return new FileInputStream(file);
        } catch (FileNotFoundException ex) {
            System.err.println(ex);
        }
        return null;
    }

    public static class Data {

        private final String name;
        private final File[] files;

        public Data(String name, File[] files) {
            this.name = name;
            this.files = files;
        }

        public String getName() {
            return name;
        }

        public File[] getFiles() {
            return files;
        }

        @Override
        public String toString() {
            StringBuilder builder = new StringBuilder();
            builder.append("\n\t").append(name).append(": ");
            for (File file : files) {
                builder.append("\n\t\t Path: ").append(file.getAbsolutePath());
//                builder.append("\n\t\t Content: ").append(getContent(file));
            }
            return builder.toString();
        }

        public boolean contains(String path) {
            for (File file : files) {
                if (file.getAbsolutePath().equalsIgnoreCase(path)) {
                    return true;
                }
            }
            return false;
        }
    }

//    public static void main(String... args) {
//        List<Data> data = getManager().getDatas();
//        System.out.println("DataSet: ");
//        for (Data d : data) {
//            System.out.println(d.toString());
//        }
//    }
}
