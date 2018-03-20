/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.dataset.engine;

import java.io.File;
import java.io.FileFilter;

/**
 *
 * @author interactive
 */
public abstract class FileManager {

    public static final String DEDFAULT_WORKING_DIRECTORY;
    public static final String FILE_SEPARATOR;
    public static final String DEDFAULT_DATASET_DIRECTORY;
    public static final String DEDFAULT_OUTPUT_DIRECTORY;
    public static final String DEDFAULT_INDEXER_DIRECTORY;
    public static final String DEDFAULT_PUBLIC_INDEXER_DIRECTORY;
    public static final String DEDFAULT_TREE_PATH;
    public static final String DEDFAULT_POINTS_DIRECTORY;
    public static final String DEDFAULT_CLUSTER_DIRECTORY;
    public static final String DEDFAULT_DATAET_LOG_DIRECTORY;
    protected final String workingDirectory;
    protected final String datasetDirectory;
    private static final String TMP_DIR;
    private final String outputDirectory;
    private final String indexerDirectory;
    private final String publicIndexerDirectory;
    private final String pointsDirectory;
    private final String clusterDirectory;
    private final String treePath;
    private final String datasetLog;

    static {
        DEDFAULT_WORKING_DIRECTORY = System.getProperty("user.dir");
        FILE_SEPARATOR = System.getProperty("file.separator");
        TMP_DIR = DEDFAULT_WORKING_DIRECTORY + FILE_SEPARATOR + "tmp";
        DEDFAULT_DATASET_DIRECTORY = "/home/interactive/Desktop/dataset"; //DEDFAULT_WORKING_DIRECTORY + FILE_SEPARATOR + "dataset";
        DEDFAULT_OUTPUT_DIRECTORY = DEDFAULT_WORKING_DIRECTORY + FILE_SEPARATOR + "output";
        DEDFAULT_POINTS_DIRECTORY = DEDFAULT_OUTPUT_DIRECTORY + FILE_SEPARATOR + "points";
        DEDFAULT_CLUSTER_DIRECTORY = DEDFAULT_OUTPUT_DIRECTORY + FILE_SEPARATOR + "clusters";
        DEDFAULT_INDEXER_DIRECTORY = DEDFAULT_OUTPUT_DIRECTORY + FILE_SEPARATOR + "indexer";
        DEDFAULT_PUBLIC_INDEXER_DIRECTORY = DEDFAULT_OUTPUT_DIRECTORY + FILE_SEPARATOR + "public_indexer";
        DEDFAULT_TREE_PATH = DEDFAULT_OUTPUT_DIRECTORY + FILE_SEPARATOR + "tree.xml";
        DEDFAULT_DATAET_LOG_DIRECTORY = DEDFAULT_OUTPUT_DIRECTORY + FILE_SEPARATOR + "log.csv";
    }

    protected FileManager(String workingDirectory, String datasetDirectory) {
        this.workingDirectory = workingDirectory;
        this.datasetDirectory = datasetDirectory;
        this.outputDirectory = workingDirectory + "output";
        this.indexerDirectory = outputDirectory + FILE_SEPARATOR + "indexer";
        this.publicIndexerDirectory = outputDirectory + FILE_SEPARATOR + "public_indexer";
        this.pointsDirectory = outputDirectory + FILE_SEPARATOR + "points";
        this.clusterDirectory = outputDirectory + FILE_SEPARATOR + "clusters";
        this.treePath = outputDirectory + FILE_SEPARATOR + "tree.xml";
        this.datasetLog = DEDFAULT_DATAET_LOG_DIRECTORY;
        File working = new File(workingDirectory);
        if (!working.exists()) {
            working.mkdirs();
        }
    }

    public String getWorkingDirectory() {
        return workingDirectory;
    }

    public String getDatasetDirectory() {
        return datasetDirectory;
    }

    public File getOutputDirectory() {
        return getFile(outputDirectory);
    }

    public File getIndexerDirectory() {
        return getFile(indexerDirectory);
    }

    public File getPublicIndexerDirectory() {
        return getFile(publicIndexerDirectory);
    }

    public File getIndexerDirectory(String name) {
        return getFile(indexerDirectory + FILE_SEPARATOR + name);
    }

    public File getPointsDirectory() {
        return getFile(pointsDirectory);
    }

    public File getClusterDirectory() {
        return getFile(clusterDirectory);
    }

    public File getTreeFile() {
        return getFile(treePath);
    }

    public File getDatasetLogFile() {
        return getFile(datasetLog);
    }

    public boolean isTreeExsist() {
        return getTreeFile().exists();
    }

    public File getFile(String path) {
        return new File(path);
    }

    public String getTMPDIR() {
        File tmp = getFile(TMP_DIR);
        if (tmp.exists()) {
            delete(tmp);
        }
        create(TMP_DIR);
        return TMP_DIR;
    }

    public boolean create(String path) {
        if (!new File(path).exists()) {
            return new File(path).mkdirs();
        }
        return true;
    }

    public boolean clearIndexer() {
        return delete(getFile(indexerDirectory));
    }

    public boolean clearPublicIndexer() {
        return delete(getFile(publicIndexerDirectory));
    }

    public boolean clearOutput() {
        return delete(getFile(outputDirectory)) && create(outputDirectory);
    }

    public File[] getDatasetDirectories() {
        return getDirectories(getFile(datasetDirectory));
    }

    public File[] getDirectories(String path) {
        return getDirectories(new File(path));
    }

    public File[] getDirectories(File file) {
        return file.listFiles(new FileFilter() {

            @Override
            public boolean accept(File pathname) {
                return pathname.isDirectory();
            }
        });
    }

    public File[] getFiles(String path) {
        return getFiles(new File(path));
    }

    public File[] getFiles(File file) {
        return file.listFiles(new FileFilter() {

            @Override
            public boolean accept(File pathname) {
                return pathname.isFile();
            }
        });
    }

    public File[] getFiles(File file, FileFilter filter) {
        return file.listFiles(filter);
    }

    /**
     *
     * By default File#delete fails for non-empty directories, it works like
     * "rm". We need something a little more brutual - this does the equivalent
     * of "rm -r"
     *
     * @param path Root File Path
     * @return true iff the file and all sub files/directories have been removed
     */
    public boolean delete(File path) {
        if (!path.exists()) {
            return true;
        }
        boolean isDeleted = true;
        if (path.isDirectory()) {
            for (File f : path.listFiles()) {
                isDeleted = isDeleted && delete(f);
            }
        }
        return isDeleted && path.delete();
    }

    public static void main(String... args) {
        for (Object object : System.getProperties().keySet()) {
            System.out.println(object + ": " + System.getProperty(object.toString()));
        }
    }
}
