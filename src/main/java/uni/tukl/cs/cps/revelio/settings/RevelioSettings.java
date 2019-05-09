package uni.tukl.cs.cps.revelio.settings;

public class RevelioSettings {

    private String filePath;

    private String ontologyPrefix;

    private String partClass;

    private String portClass;

    /**
     * Create a new Revelio Settings instance with default values for Part and Port classes
     */
    public RevelioSettings() {
        this.partClass = "Part";
        this.portClass = "Port";
    }

    /**
     * Create a new Revelio Settings instance with default values for Part and Port classes
     */
    public RevelioSettings(String filePath, String ontologyPrefix) {
        this();
        this.filePath = filePath;
        this.ontologyPrefix = ontologyPrefix;
    }

    /**
     * Create a new Revelio Settings instance with custom values for Part and Port classes
     */
    public RevelioSettings(String filePath, String ontologyPrefix, String partClass, String portClass) {
        this.filePath = filePath;
        this.ontologyPrefix = ontologyPrefix;
        this.partClass = partClass;
        this.portClass = portClass;
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
     * The URI ontology prefix
     * @return ontology prefix
     */
    public String getOntologyPrefix() {
        return ontologyPrefix;
    }

    /**
     * SysML file path
     * @return file path
     */
    public String getFilePath() {
        return filePath;
    }

}
