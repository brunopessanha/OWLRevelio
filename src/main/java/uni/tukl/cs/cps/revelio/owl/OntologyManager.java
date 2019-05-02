package uni.tukl.cs.cps.revelio.owl;

import org.semanticweb.owlapi.apibinding.OWLManager;
import org.semanticweb.owlapi.model.*;
import uni.tukl.cs.cps.revelio.parser.SysML2OWLParser;
import uni.tukl.cs.cps.revelio.sysML.Association;
import uni.tukl.cs.cps.revelio.sysML.Block;
import uni.tukl.cs.cps.revelio.parser.Enums;
import uni.tukl.cs.cps.revelio.sysML.OwnedAttribute;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class OntologyManager {

    private final OWLOntologyManager ontologyManager;
    private final OWLDataFactory dataFactory;

    private List<OWLClassAxiom> classAxioms;
    private List<OWLObjectPropertyAxiom> objectPropertyAxioms;
    private List<OWLDataPropertyAxiom> dataPropertyAxioms;
    private List<OWLIndividualAxiom> individualAxioms;
    private List<OWLAxiom> axioms;

    private String rootClass;
    private String ontologyPrefix;

    private SysML2OWLParser parser;

    public OntologyManager(String ontologyPrefix, String rootClass, SysML2OWLParser parser) {
        this.ontologyManager = OWLManager.createOWLOntologyManager();
        this.dataFactory = ontologyManager.getOWLDataFactory();
        this.ontologyPrefix = ontologyPrefix;
        this.rootClass = rootClass;
        this.parser = parser;
        this.classAxioms = new ArrayList<>();
        this.objectPropertyAxioms = new ArrayList<>();
        this.dataPropertyAxioms = new ArrayList<>();
        this.individualAxioms = new ArrayList<>();
        this.generateAxioms();
    }

    public String getRootClass() {
        return rootClass;
    }

    public String getOntologyPrefix() {
        return ontologyPrefix;
    }

    public IRI getDataPropertyIRI(String name) {
        return this.getIRI("has" + name);
    }

    public IRI getIRI(String name) {
        return IRI.create(ontologyPrefix, name);
    }

    private OWLClass getOWLClass(String name) {
        return dataFactory.getOWLClass(getIRI(name));
    }

    public void generateAxioms() {
        for (Block block : parser.getBlockMap().values()) {

            OWLClass owlClass = getOWLClass(block.getName());
            OWLClass owlParentClass = getOWLClass(block.getSuperClass());

            OWLClassAxiom axiom = dataFactory.getOWLSubClassOfAxiom(owlClass, owlParentClass);

            classAxioms.add(axiom);

            getBlockAttributesAxioms(block.getAttributes(), owlClass);
        }

        for (Association association : parser.getAssociations()) {

            OWLClass owlOwnerClass = getOWLClass(parser.getBlockMap().get(association.getOwner().getType()).getName());
            OWLClass owlOwnedClass = getOWLClass(parser.getBlockMap().get(association.getOwned().getType()).getName());

            OWLObjectProperty hasPartRelation = dataFactory.getOWLObjectProperty(getIRI(Enums.Association.HasPart.toString()));
            OWLClassExpression classExpression = getOWLClassExpression(association, owlOwnedClass, hasPartRelation);

            OWLClassAxiom axiom = dataFactory.getOWLSubClassOfAxiom(owlOwnerClass, classExpression);

            classAxioms.add(axiom);
        }
    }

    private void getBlockAttributesAxioms(List<OwnedAttribute> attributes, OWLClass blockClass) {
        for (OwnedAttribute attribute : attributes) {

            OWLDataProperty dataProperty = dataFactory.getOWLDataProperty(getDataPropertyIRI(attribute.getName()));

            OWLDataPropertyRangeAxiom rangeAxiom = dataFactory.getOWLDataPropertyRangeAxiom(dataProperty, attribute.getDataType().getDatatype(dataFactory));

            dataPropertyAxioms.add(rangeAxiom);

            OWLClassExpression classExpression = dataFactory.getOWLDataSomeValuesFrom(dataProperty, attribute.getDataType());
            OWLClassAxiom dataPropertyRestrictionAxiom = dataFactory.getOWLSubClassOfAxiom(blockClass, classExpression);

            classAxioms.add(dataPropertyRestrictionAxiom);
        }
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

    public Stream<OWLClassAxiom> classAxioms() {
        return classAxioms.stream();
    }

    public Stream<OWLObjectPropertyAxiom> objectPropertyAxioms() {
        return objectPropertyAxioms.stream();
    }

    public Stream<OWLDataPropertyAxiom> dataPropertyAxioms() {
        return dataPropertyAxioms.stream();
    }

    public Stream<OWLIndividualAxiom> individualAxioms() {
        return individualAxioms.stream();
    }

    public Stream<OWLAxiom> axioms() {
        axioms = new ArrayList<>();
        axioms.addAll(classAxioms().collect(Collectors.toList()));
        axioms.addAll(objectPropertyAxioms().collect(Collectors.toList()));
        axioms.addAll(dataPropertyAxioms().collect(Collectors.toList()));
        axioms.addAll(individualAxioms().collect(Collectors.toList()));
        return axioms.stream();
    }

    public void saveOntology(File file) throws OWLOntologyStorageException, OWLOntologyCreationException {
        OWLOntology ontology = ontologyManager.createOntology(IRI.create(file.toURI()));
        ontology.addAxioms(axioms());
        ontologyManager.saveOntology(ontology);
    }
}
