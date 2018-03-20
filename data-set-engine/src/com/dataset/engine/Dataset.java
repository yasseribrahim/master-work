/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.dataset.engine;

import java.io.FileFilter;
import java.util.List;

/**
 *
 * @author interactive
 */
public class Dataset {

    private final DatasetManager manager;
    private List<DatasetManager.Data> dataset;

    public static Dataset getInstance(String workingDirectory, String datasetDirectory) {
        return new Dataset(datasetDirectory, workingDirectory);
    }

    public static Dataset getInstance(String workingDirectory, String datasetDirectory, FileFilter filter) {
        return new Dataset(datasetDirectory, workingDirectory, filter);
    }

    public Dataset() {
        manager = null;
    }

    private Dataset(String datasetDirectory, String workingDirectory) {
        manager = DatasetManager.getManager(datasetDirectory, workingDirectory);
        dataset = manager.getDatas();
    }

    private Dataset(String datasetDirectory, String workingDirectory, FileFilter filter) {
        manager = DatasetManager.getManager(datasetDirectory, workingDirectory);
        dataset = manager.getDatas(filter);
    }

    public DatasetManager getManager() {
        return manager;
    }

    public List<DatasetManager.Data> getDataset() {
        return dataset;
    }

    public void setDataset(List<DatasetManager.Data> dataset) {
        this.dataset = dataset;
    }

    public int getSize() {
        int size = 0;
        for (DatasetManager.Data data : dataset) {
            size += data.getFiles().length;
        }
        return size;
    }
}
