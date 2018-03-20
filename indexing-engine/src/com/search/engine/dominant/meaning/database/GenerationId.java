/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.search.engine.dominant.meaning.database;

import java.util.Calendar;

/**
 *
 * @author interactive
 */
public class GenerationId {

    public synchronized static String generate() {
        Calendar calendar = Calendar.getInstance();
        try {
            Thread.sleep(10);
        } catch (Exception ex) {
            System.err.println(ex);
        }
        return calendar.getTimeInMillis() + "";
    }

}
