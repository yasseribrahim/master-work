/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.search.engine.dominant.meaning.database;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author interactive
 */
public class CommandBean {
    private Connection connection;
    private PreparedStatement statement;
    private List parameters;
    private String query;

    public CommandBean() {
        this.parameters = new ArrayList();
    }

    public Connection getConnection() {
        return connection;
    }

    public void setConnection(Connection connection) {
        this.connection = connection;
    }

    public PreparedStatement getStatement() {
        return statement;
    }

    public void setStatement(PreparedStatement statement) {
        this.statement = statement;
    }

    public String getQuery() {
        return query;
    }

    public void setQuery(String query) {
        this.query = query;
    }

    public void setParameters(List parameters) {
        this.parameters = parameters;
    }
    
    public List getParameters() {
        return parameters;
    }
    
    public int executeQuery() throws SQLException {
        statement = connection.prepareStatement(query);
        int index = 1;
        for (Object parameter : parameters) {
            statement.setObject(index, parameter);
            index++;
        }
        int result = statement.executeUpdate();
        return result;
    }
    
    public ResultSet execute() throws SQLException {
        statement = connection.prepareStatement(query);
        int index = 1;
        for (Object parameter : parameters) {
            statement.setObject(index, parameter);
            index++;
        }
        ResultSet result = statement.executeQuery();
        return result;
    }
}
