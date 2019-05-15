package com.github.brunopessanha.revelio.settings;

public class RevelioSettings {

    private String filePath;

    private String ontologyIRI;

    private String partClass;

    private String portClass;

    private String connectionClass;

    /**
     * Create a new Revelio Settings instance with default values for Part and Port classes
     */
    public RevelioSettings() {
        this.partClass = "Part";
        this.portClass = "Port";
        this.connectionClass =  "Connection";
    }

    /**
     * Create a new Revelio Settings instance with default values for Part and Port classes
     */
    public RevelioSettings(String filePath, String ontologyIRI) {
        this();
        this.filePath = filePath;
        this.ontologyIRI = ontologyIRI;
    }

    /**
     * Create a new Revelio Settings instance with custom values for Part and Port classes
     */
    public RevelioSettings(String filePath, String ontologyIRI, String partClass, String portClass, String connectionClass) {
        this.filePath = filePath;
        this.ontologyIRI = ontologyIRI;
        this.partClass = partClass;
        this.portClass = portClass;
        this.connectionClass = connectionClass;
    }

    /**
     * The root class that will have SysML Ports as sub classes
     * @return port class
     */
    public String getPortClass() {
        return portClass;
    }

    /**
     * The root class that will have SysML Blocks as sub classes
     * @return part class
     */
    public String getPartClass() {
        return partClass;
    }

    /**
     * The root class that will have SysML connections between ports as sub classes
     * @return part class
     */
    public String getConnectionClass() {
        return connectionClass;
    }

    /**
     * The ontology IRI
     * @return ontology IRI
     */
    public String getOntologyIRI() {
        return ontologyIRI;
    }

    /**
     * SysML file path
     * @return file path
     */
    public String getFilePath() {
        return filePath;
    }

}
