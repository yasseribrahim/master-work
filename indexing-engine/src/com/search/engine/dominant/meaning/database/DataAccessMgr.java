package com.search.engine.dominant.meaning.database;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class DataAccessMgr {

    private static final DataAccessMgr MGR = new DataAccessMgr();

    private static final String DATABASE;
    private static final String HOST;
    private static final String PORT;
    private static final String USERNAME;
    private static final String PASSWORD;
    private static final String URL;

    static {
        DATABASE = "master";
        HOST = "127.0.0.1";
        PORT = "3306";
        USERNAME = "root";
        PASSWORD = "root";
        try {
            Class.forName("com.mysql.jdbc.Driver");
        } catch (Exception ex) {
            System.err.println(ex);
        }
        URL = String.format("jdbc:mysql://%s:%s/%s", HOST, PORT, DATABASE);
    }

    private DataAccessMgr() {

    }

    public static DataAccessMgr getCurrentInstance() {
        return MGR;
    }

    public Connection getConnection() throws SQLException {
        Connection connection = DriverManager.getConnection(URL, USERNAME, PASSWORD);
        return connection;
    }

    public Concept saveConcept(Concept concept) {
        CommandBean command = new CommandBean();
        Connection connection = null;
        List parameters = new ArrayList();

        String id = GenerationId.generate();
        parameters.add(id);
        parameters.add(concept.getValue());
        parameters.add(concept.getName());

        try {
            connection = getConnection();
            command.setConnection(connection);
            command.setParameters(parameters);
            command.setQuery(Concept.INSERT);

            int result = command.executeQuery();
            if (result > 0) {
                concept.setId(id);
                return concept;
            }
        } catch (SQLException ex) {
            System.err.println(ex);
        } finally {
            try {
                if (connection != null) {
                    connection.close();
                }
            } catch (SQLException ex) {
                System.err.println(ex);
            }
        }
        concept.setId(null);
        return concept;
    }

    public Result saveResult(Result result) {
        CommandBean command = new CommandBean();
        Connection connection = null;
        List parameters = new ArrayList();

        String id = GenerationId.generate();
        parameters.add(id);
        parameters.add(result.getConcept());
        parameters.add(result.getConceptName());
        parameters.add(result.getSize());
        parameters.add(result.getHit());
        parameters.add(result.getExpected());
        parameters.add(result.getRetrieved());
        parameters.add(result.getRecall());
        parameters.add(result.getPrecision());
        parameters.add(result.getF1());

        try {
            connection = getConnection();
            command.setConnection(connection);
            command.setParameters(parameters);
            command.setQuery(Result.INSERT);

            int r = command.executeQuery();
            if (r > 0) {
                result.setId(id);
                return result;
            }
        } catch (SQLException ex) {
            System.err.println(ex);
        } finally {
            try {
                if (connection != null) {
                    connection.close();
                }
            } catch (SQLException ex) {
                System.err.println(ex);
            }
        }
        result.setId(null);
        return result;
    }

    public Tree saveTree(Tree tree) {
        CommandBean command = new CommandBean();
        Connection connection = null;
        List parameters = new ArrayList();

        String id = GenerationId.generate();
        parameters.add(id);
        parameters.add(tree.getConcept());
        parameters.add(tree.getConceptName());
        parameters.add(tree.getTerm());
        parameters.add(tree.getWeight());

        try {
            connection = getConnection();
            command.setConnection(connection);
            command.setParameters(parameters);
            command.setQuery(Tree.INSERT);

            int result = command.executeQuery();
            if (result > 0) {
                tree.setId(id);
                return tree;
            }
        } catch (SQLException ex) {
            System.err.println(ex);
        } finally {
            try {
                if (connection != null) {
                    connection.close();
                }
            } catch (SQLException ex) {
                System.err.println(ex);
            }
        }
        tree.setId(null);
        return tree;
    }

    public boolean updateConcept(Concept concept) {
        CommandBean command = new CommandBean();
        Connection connection = null;
        List parameters = new ArrayList();

        parameters.add(concept.getName());
        parameters.add(concept.getValue());

        try {
            connection = getConnection();
            command.setConnection(connection);
            command.setQuery(Concept.UPDATE);
            command.setParameters(parameters);

            return command.executeQuery() > 0;
        } catch (SQLException ex) {
            System.err.println(ex);
        } finally {
            try {
                if (connection != null) {
                    connection.close();
                }
            } catch (SQLException ex) {
                System.err.println(ex);
            }
        }
        return false;
    }

    public Concept getConcept(String id) {
        CommandBean command = new CommandBean();
        Connection connection = null;
        List parameters = new ArrayList();

        parameters.add(id);

        try {
            connection = getConnection();
            command.setConnection(connection);
            command.setQuery(Concept.SELECT);
            command.setParameters(parameters);

            ResultSet result = command.execute();
            while (result.next()) {
                return parseConcept(result, true);
            }
        } catch (SQLException ex) {
            System.err.println(ex);
        } finally {
            try {
                if (connection != null) {
                    connection.close();
                }
            } catch (SQLException ex) {
                System.err.println(ex);
            }
        }
        return null;
    }

    public ConceptDetails getConceptDetails(String id) {
        CommandBean command = new CommandBean();
        Connection connection = null;
        List parameters = new ArrayList();

        parameters.add(id);

        try {
            connection = getConnection();
            command.setConnection(connection);
            command.setQuery(ConceptDetails.SELECT);
            command.setParameters(parameters);

            ResultSet result = command.execute();
            while (result.next()) {
                return parseConceptDetails(result);
            }
        } catch (SQLException ex) {
            System.err.println(ex);
        } finally {
            try {
                if (connection != null) {
                    connection.close();
                }
            } catch (SQLException ex) {
                System.err.println(ex);
            }
        }
        return null;
    }

    public ConceptDetails saveConceptDetails(ConceptDetails details) {
        CommandBean command = new CommandBean();
        Connection connection = null;
        List parameters = new ArrayList();

        String id = GenerationId.generate();
        parameters.add(id);
        parameters.add(details.getConceptId().getId());
        parameters.add(details.getTerm());
        parameters.add(details.getDocument());
        parameters.add(details.getFrequent());

        try {
            connection = getConnection();
            command.setConnection(connection);
            command.setParameters(parameters);
            command.setQuery(ConceptDetails.INSERT);

            int result = command.executeQuery();
            if (result > 0) {
                details.setId(id);
                return details;
            }
        } catch (SQLException ex) {
            System.err.println(ex);
        } finally {
            try {
                if (connection != null) {
                    connection.close();
                }
            } catch (SQLException ex) {
                System.err.println(ex);
            }
        }
        details.setId(null);
        return details;
    }

    public List<Concept> getConcepts() {
        CommandBean command = new CommandBean();
        Connection connection = null;

        try {
            connection = getConnection();
            command.setConnection(connection);
            command.setQuery(Concept.SELECT_ALL);

            ResultSet result = command.execute();
            List<Concept> concepts = new ArrayList<>();
            while (result.next()) {
                concepts.add(parseConcept(result, false));
            }
            return concepts;
        } catch (SQLException ex) {
            System.err.println(ex);
        } finally {
            try {
                if (connection != null) {
                    connection.close();
                }
            } catch (SQLException ex) {
                System.err.println(ex);
            }
        }
        return new ArrayList<>();
    }

    public List<String> documents(String conceptId, String... terms) {
        CommandBean command = new CommandBean();
        Connection connection = null;
        List parameters = new ArrayList();

        parameters.add(conceptId);

        try {
            connection = getConnection();
            command.setConnection(connection);
            command.setQuery(ConceptDetails.COUNT.replace("TERMS", concate(terms)));
            command.setParameters(parameters);

            ResultSet result = command.execute();
            List<String> documents = new ArrayList<>();
            while (result.next()) {
                documents.add(result.getString(1));
            }
            return documents;
        } catch (SQLException ex) {
            System.err.println(ex);
        } finally {
            try {
                if (connection != null) {
                    connection.close();
                }
            } catch (SQLException ex) {
                System.err.println(ex);
            }
        }
        return new ArrayList<>();
    }

    private String concate(String... values) {
        StringBuilder builder = new StringBuilder();
        for (int i = 0; i < values.length; i++) {
            builder.append("'").append(values[i]).append("'");
            if (i < (values.length - 1)) {
                builder.append(", ");
            }
        }
        return builder.toString();
    }

    public List<ConceptDetails> getDetails(String conceptId) {
        CommandBean command = new CommandBean();
        Connection connection = null;
        List parameters = new ArrayList();

        parameters.add(conceptId);

        try {
            connection = getConnection();
            command.setConnection(connection);
            command.setQuery(ConceptDetails.SELECT_CONCEPT);
            command.setParameters(parameters);

            ResultSet result = command.execute();
            List<ConceptDetails> details = new ArrayList<>();
            while (result.next()) {
                details.add(parseConceptDetails(result));
            }
            return details;
        } catch (SQLException ex) {
            System.err.println(ex);
        } finally {
            try {
                if (connection != null) {
                    connection.close();
                }
            } catch (SQLException ex) {
                System.err.println(ex);
            }
        }
        return new ArrayList<>();
    }

    public List<Tree> getTree(String concept) {
        CommandBean command = new CommandBean();
        Connection connection = null;
        List parameters = new ArrayList();

        parameters.add(concept);

        try {
            connection = getConnection();
            command.setConnection(connection);
            command.setQuery(Tree.SELECT);
            command.setParameters(parameters);

            ResultSet result = command.execute();
            List<Tree> tree = new ArrayList<>();
            while (result.next()) {
                tree.add(parseTree(result));
            }
            return tree;
        } catch (SQLException ex) {
            System.err.println(ex);
        } finally {
            try {
                if (connection != null) {
                    connection.close();
                }
            } catch (SQLException ex) {
                System.err.println(ex);
            }
        }
        return new ArrayList<>();
    }

    public List<Frequent> getFrequents(String concept) {
        CommandBean command = new CommandBean();
        Connection connection = null;
        List parameters = new ArrayList();

        parameters.add(concept);

        try {
            connection = getConnection();
            command.setConnection(connection);
            command.setQuery(Frequent.SELECT);
            command.setParameters(parameters);

            ResultSet result = command.execute();
            List<Frequent> frequents = new ArrayList<>();
            while (result.next()) {
                frequents.add(parseFrequent(result));
            }
            return frequents;
        } catch (SQLException ex) {
            System.err.println(ex);
        } finally {
            try {
                if (connection != null) {
                    connection.close();
                }
            } catch (SQLException ex) {
                System.err.println(ex);
            }
        }
        return new ArrayList<>();
    }

    public Concept parseConcept(ResultSet set, boolean lazy) {
        try {
            String id = set.getString(1);
            String value = set.getString(2);
            String name = set.getString(3);
            Concept concept = new Concept(id, value, name);
            concept.setConceptDetailsList(lazy ? getDetails(id) : new ArrayList<>());
            return concept;
        } catch (Exception ex) {
            System.err.println(ex);
        }
        return null;
    }

    public ConceptDetails parseConceptDetails(ResultSet set) {
        try {
            String id = set.getString(1);
            String conceptId = set.getString(2);
            String term = set.getString(3);
            String document = set.getString(4);
            Integer frequent = set.getInt(5);
            ConceptDetails details = new ConceptDetails(id, term, document, frequent);
            details.setConceptId(getConcept(conceptId));
            return details;
        } catch (Exception ex) {
            System.err.println(ex);
        }
        return null;
    }

    public Frequent parseFrequent(ResultSet set) {
        try {
            String concept = set.getString(1);
            String termId = set.getString(2);
            String term = set.getString(3);
            Integer frequent = set.getInt(4);
            Integer count = set.getInt(5);
            return new Frequent(concept, termId, term, frequent, count);
        } catch (Exception ex) {
            System.err.println(ex);
        }
        return null;
    }

    public Result parseResult(ResultSet set) {
        try {
            String id = set.getString(1);
            String concept = set.getString(2);
            String conceptName = set.getString(3);
            int size = set.getInt(4);
            int hit = set.getInt(5);
            int expected = set.getInt(6);
            int retrieved = set.getInt(7);
            float recall = set.getFloat(8);
            float precision = set.getFloat(9);
            float f1 = set.getFloat(10);
            return new Result(id, concept, conceptName, size, hit, expected, retrieved, precision, recall, f1);
        } catch (Exception ex) {
            System.err.println(ex);
        }
        return null;
    }

    public Tree parseTree(ResultSet set) {
        try {
            String id = set.getString(1);
            String concept = set.getString(2);
            String conceptName = set.getString(3);
            String term = set.getString(4);
            float weight = set.getInt(5);
            return new Tree(id, concept, conceptName, term, weight);
        } catch (Exception ex) {
            System.err.println(ex);
        }
        return null;
    }
}
