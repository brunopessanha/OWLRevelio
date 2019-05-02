package uni.tukl.cs.cps.revelio.owl;

import org.semanticweb.owlapi.apibinding.OWLManager;
import org.semanticweb.owlapi.model.*;
import uni.tukl.cs.cps.revelio.Revelio;
import uni.tukl.cs.cps.revelio.sysML.Association;
import uni.tukl.cs.cps.revelio.sysML.Block;
import uni.tukl.cs.cps.revelio.sysML.Enums;
import uni.tukl.cs.cps.revelio.sysML.OwnedEnd;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class OntologyManager {

    private final OWLOntologyManager ontologyManager;
    private final OWLDataFactory dataFactory;
    private OWLOntology ontology;

    private List<OWLClassAxiom> classAxioms;
    private List<OWLObjectPropertyAxiom> objectPropertyAxioms;
    private List<OWLDataPropertyAxiom> dataPropertyAxioms;
    private List<OWLIndividualAxiom> individualAxioms;
    private List<OWLAxiom> axioms;

    private String rootClass;
    private String ontologyPrefix;

    private Revelio revelio;

    public OntologyManager(String ontologyPrefix, String rootClass, Revelio revelio) {
        this.ontologyManager = OWLManager.createOWLOntologyManager();
        this.dataFactory = ontologyManager.getOWLDataFactory();
        this.ontologyPrefix = ontologyPrefix;
        this.rootClass = rootClass;
        this.revelio = revelio;
    }

    public String getRootClass() {
        return rootClass;
    }

    public String getOntologyPrefix() {
        return ontologyPrefix;
    }

    public IRI getIRI(String name) {
        return IRI.create(ontologyPrefix, name);
    }

    private OWLClass getOWLClass(String name) {
        return dataFactory.getOWLClass(getIRI(name));
    }

    public Stream<OWLClassAxiom> classAxioms() {
        if (classAxioms == null) {
            classAxioms = new ArrayList<>();

            for (Block block : revelio.getBlockMap().values()) {
                OWLClass owlClass = getOWLClass(block.getName());
                OWLClass owlParentClass = getOWLClass(block.getSuperClass());

                OWLClassAxiom axiom = dataFactory.getOWLSubClassOfAxiom(owlClass, owlParentClass);

                classAxioms.add(axiom);
            }

            for (Association association : revelio.getAssociations()) {

                OWLClass owlOwnerClass = getOWLClass(revelio.getBlockMap().get(association.getOwner().getType()).getName());
                OWLClass owlOwnedClass = getOWLClass(revelio.getBlockMap().get(association.getOwned().getType()).getName());

                OWLObjectProperty hasPartRelation = dataFactory.getOWLObjectProperty(getIRI(Enums.Association.HasPart.toString()));
                OWLClassExpression classExpression = getOWLClassExpression(association, owlOwnedClass, hasPartRelation);

                OWLClassAxiom axiom = dataFactory.getOWLSubClassOfAxiom(owlOwnerClass, classExpression);

                classAxioms.add(axiom);
            }
        }

        return classAxioms.stream();
    }

    private OWLClassExpression getOWLClassExpression(Association association, OWLClass owlOwnedClass, OWLObjectProperty hasPartRelation) {
        OWLClassExpression classExpression;

        if (association.hasExactCardinalityRestriction()) {

            int cardinality = Integer.parseInt(association.getOwned().getLowerValue().getValue());
            classExpression = dataFactory.getOWLObjectExactCardinality(cardinality, hasPartRelation, owlOwnedClass);

        } else if (association.hasMinCardinalityRestriction()) {

            int cardinality = Integer.parseInt(association.getOwned().getLowerValue().getValue());
            classExpression = dataFactory.getOWLObjectMinCardinality(cardinality, hasPartRelation, owlOwnedClass);

        } else if (association.hasMaxCardinalityRestriction()) {

            int cardinality = Integer.parseInt(association.getOwned().getUpperValue().getValue());
            classExpression = dataFactory.getOWLObjectMaxCardinality(cardinality, hasPartRelation, owlOwnedClass);
        } else {

            classExpression = dataFactory.getOWLObjectSomeValuesFrom(hasPartRelation, owlOwnedClass);
        }

        return classExpression;
    }

    public Stream<OWLObjectPropertyAxiom> objectPropertyAxioms() {
        if (objectPropertyAxioms == null) {
            objectPropertyAxioms = new ArrayList<>();
        }
        return objectPropertyAxioms.stream();
    }

    public Stream<OWLDataPropertyAxiom> dataPropertyAxioms() {
        if (dataPropertyAxioms == null) {
            dataPropertyAxioms = new ArrayList<>();
        }
        return dataPropertyAxioms.stream();
    }

    public Stream<OWLIndividualAxiom> individualAxioms() {
        if (individualAxioms == null) {
            individualAxioms = new ArrayList<>();
        }
        return individualAxioms.stream();
    }

    public Stream<OWLAxiom> axioms() {
        if (axioms == null) {
            axioms = new ArrayList<>();
            axioms.addAll(classAxioms().collect(Collectors.toList()));
            axioms.addAll(objectPropertyAxioms().collect(Collectors.toList()));
            axioms.addAll(dataPropertyAxioms().collect(Collectors.toList()));
            axioms.addAll(individualAxioms().collect(Collectors.toList()));
        }
        return axioms.stream();
    }

    public void saveOntology(File file) throws OWLOntologyStorageException, OWLOntologyCreationException {
        ontology = ontologyManager.createOntology(IRI.create(file.toURI()));
        ontology.addAxioms(axioms());
        ontologyManager.saveOntology(ontology);
    }
}
