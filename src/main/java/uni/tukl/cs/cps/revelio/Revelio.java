package uni.tukl.cs.cps.revelio;

import org.semanticweb.owlapi.model.*;
import uni.tukl.cs.cps.revelio.exceptions.InvalidSysMLFileException;
import uni.tukl.cs.cps.revelio.owl.IOntologyCreator;
import uni.tukl.cs.cps.revelio.owl.OntologyCreator;
import uni.tukl.cs.cps.revelio.parser.ISysMLParser;
import uni.tukl.cs.cps.revelio.parser.SysMLParser;
import uni.tukl.cs.cps.revelio.settings.RevelioSettings;

import java.io.File;
import java.util.stream.Stream;

public class Revelio {

    private IOntologyCreator ontologyManager;

    private ISysMLParser parser;

    /**
     * Create a new instance of Revelio which will parse a SysML file and generate OWL axioms
     * @param settings Settings for part, port, SysML file path and Ontology URI prefix
     * @throws InvalidSysMLFileException
     */
    public Revelio(RevelioSettings settings) throws InvalidSysMLFileException {
        this.parser = new SysMLParser(settings.getFilePath(), settings.getPartClass());
        this.ontologyManager = new OntologyCreator(settings.getOntologyPrefix(), settings.getPortClass(), parser);
        this.ontologyManager.generateAxioms();
    }

    /**
     * Create a new instance of Revelio which will parse a SysML file and generate OWL axioms
     * @param filePath the path to the SysML file
     * @param ontologyPrefix the prefix for the generated ontology
     * @throws InvalidSysMLFileException
     */
    public Revelio(String filePath, String ontologyPrefix) throws InvalidSysMLFileException {
        this(new RevelioSettings(filePath, ontologyPrefix));
    }

    /**
     * Class axioms created by Revelio. The following SysML Block Diagram concepts are transformed into classes:
     * - SysML Blocks: under a super class defined in settings (default: Part)
     * - SysML Ports: under a super class defined in settings (default: Port)
     * @return OWL Data Property Axioms
     */
    public Stream<OWLClassAxiom> classAxioms() {
        return ontologyManager.classAxioms();
    }

    /**
     * Object Property axioms created by Revelio.
     * @return OWL Object Property Axioms
     */
    public Stream<OWLObjectPropertyAxiom> objectPropertyAxioms() {
        return ontologyManager.objectPropertyAxioms();
    }

    /**
     * Data Property axioms created by Revelio. The following SysML Block Diagram concepts are transformed into data properties:
     * - SysML Block properties including their data types
     * @return OWL Data Property Axioms
     */
    public Stream<OWLDataPropertyAxiom> dataPropertyAxioms() {
        return ontologyManager.dataPropertyAxioms();
    }

    /**
     * Individual axioms created by Revelio. The following SysML Internal Block Diagram concepts are transformed into individuals:
     * - SysML Blocks
     * - SysML Ports
     * @return OWL Individual Axioms
     */
    public Stream<OWLIndividualAxiom> individualAxioms() {
        return ontologyManager.individualAxioms();
    }

    /**
     * Annotation axioms created by Revelio. The following SysML concepts are transformed into annotations:
     * - SysML Comments
     * @return OWL Annotation Axioms
     */
    public Stream<OWLAnnotationAxiom> annotationAxioms() {
        return ontologyManager.annotationAxioms();
    }

    /**
     * All axioms created by Revelio
     * @return OWL Axioms
     */
    public Stream<OWLAxiom> axioms() {
        return ontologyManager.axioms();
    }

    /**
     * Save Axioms to an Ontology file in OWL format
     * @param file the OWL ontology file
     * @throws OWLOntologyCreationException
     * @throws OWLOntologyStorageException
     */
    public void saveOntology(File file) throws OWLOntologyCreationException, OWLOntologyStorageException {
        ontologyManager.saveOntology(file);
    }

}
