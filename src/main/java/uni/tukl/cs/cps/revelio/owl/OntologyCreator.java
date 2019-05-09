package uni.tukl.cs.cps.revelio.owl;

import org.semanticweb.owlapi.apibinding.OWLManager;
import org.semanticweb.owlapi.model.*;
import uni.tukl.cs.cps.revelio.parser.ISysMLParser;
import uni.tukl.cs.cps.revelio.sysML.*;
import uni.tukl.cs.cps.revelio.parser.Enums;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Stream;

public class OntologyCreator implements IOntologyCreator {

    private final OWLOntologyManager ontologyManager;
    private final OWLDataFactory dataFactory;

    private List<OWLClassAxiom> classAxioms;
    private List<OWLObjectPropertyAxiom> objectPropertyAxioms;
    private List<OWLDataPropertyAxiom> dataPropertyAxioms;
    private List<OWLIndividualAxiom> individualAxioms;
    private List<OWLAnnotationAxiom> annotationAxioms;

    private String portClass;
    private String ontologyPrefix;
    private Map<String, OWLIndividual> individuals;

    private ISysMLParser parser;

    public OntologyCreator(String ontologyPrefix, String portClass, ISysMLParser parser) {
        this.ontologyManager = OWLManager.createOWLOntologyManager();
        this.dataFactory = ontologyManager.getOWLDataFactory();
        this.ontologyPrefix = ontologyPrefix;
        this.portClass = portClass;
        this.parser = parser;
        this.classAxioms = new ArrayList<>();
        this.objectPropertyAxioms = new ArrayList<>();
        this.dataPropertyAxioms = new ArrayList<>();
        this.individualAxioms = new ArrayList<>();
        this.annotationAxioms = new ArrayList<>();
        this.individuals = new HashMap<>();
    }

    private OWLObjectProperty getHasPartRelation() {
        return dataFactory.getOWLObjectProperty(getIRI(Enums.Relation.HasPart.toString()));
    }

    private OWLObjectProperty getHasPortRelation() {
        return dataFactory.getOWLObjectProperty(getIRI(Enums.Relation.HasPort.toString()));
    }

    private IRI getDataPropertyIRI(String name) {
        return this.getIRI("has" + name);
    }

    private IRI getIRI(String name) {
        return IRI.create(ontologyPrefix, name);
    }

    private OWLClass getOWLClass(String name) {
        return dataFactory.getOWLClass(getIRI(name));
    }

    private OWLAnnotationAxiom getCommentAxiom(String comment, IRI iri) {
        OWLLiteral literal = dataFactory.getOWLLiteral(comment);
        OWLAnnotationProperty commentAnnotation = dataFactory.getRDFSComment();

        OWLAnnotation annotation = dataFactory.getOWLAnnotation(commentAnnotation, literal);
        OWLAnnotationAxiom commentAxiom = dataFactory.getOWLAnnotationAssertionAxiom(iri, annotation);

        return commentAxiom;
    }

    public void generateAxioms() {
        for (Block block : parser.getBlockMap().values()) {

            OWLClass owlBlockClass = getOWLClass(block.getName());
            OWLClass owlParentClass = getOWLClass(block.getSuperClass());

            OWLClassAxiom axiom = dataFactory.getOWLSubClassOfAxiom(owlBlockClass, owlParentClass);

            classAxioms.add(axiom);

            getCommentsAxioms(block.getComments(), owlBlockClass.getIRI());

            getBlockAttributesAxioms(block.getAttributes(), owlBlockClass);

            getBlockConnectorsAxioms(block.getConnectors());

            getBlockPortsAxioms(block.getPorts(), owlBlockClass);
        }

        for (Association association : parser.getAssociations()) {

            OWLClass owlOwnerClass = getOWLClass(parser.getBlockMap().get(association.getOwner().getType()).getName());
            OWLClass owlOwnedClass = getOWLClass(parser.getBlockMap().get(association.getOwned().getType()).getName());

            OWLClassExpression classExpression = getOWLClassExpression(association, owlOwnedClass);

            OWLClassAxiom axiom = dataFactory.getOWLSubClassOfAxiom(owlOwnerClass, classExpression);

            classAxioms.add(axiom);
        }
    }


    private void getCommentsAxioms(List<OwnedComment> comments, IRI iri) {
        for (OwnedComment comment : comments) {
            annotationAxioms.add(getCommentAxiom(comment.getBody(), iri));
        }
    }

    private void getBlockAttributesAxioms(List<OwnedAttribute> attributes, OWLClass blockClass) {
        for (OwnedAttribute attribute : attributes) {
            if (attribute.getDataType() != null) { // data property attribute

                OWLDataProperty dataProperty = dataFactory.getOWLDataProperty(getDataPropertyIRI(attribute.getName()));

                OWLDataPropertyRangeAxiom rangeAxiom = dataFactory.getOWLDataPropertyRangeAxiom(dataProperty, attribute.getDataType().getDatatype(dataFactory));

                dataPropertyAxioms.add(rangeAxiom);

                OWLClassExpression classExpression = dataFactory.getOWLDataSomeValuesFrom(dataProperty, attribute.getDataType());
                OWLClassAxiom dataPropertyRestrictionAxiom = dataFactory.getOWLSubClassOfAxiom(blockClass, classExpression);

                classAxioms.add(dataPropertyRestrictionAxiom);
            } else { //instance attribute of internal block diagram

                OWLNamedIndividual individual = dataFactory.getOWLNamedIndividual(getIRI(attribute.getName()));
                OWLClass individualType = getOWLClass(parser.getBlockMap().get(attribute.getType()).getName());

                individuals.put(attribute.getId(), individual);

                individualAxioms.add(dataFactory.getOWLClassAssertionAxiom(individualType, individual));

                getCommentsAxioms(attribute.getComments(), individual.getIRI());
            }
        }
    }

    private void getBlockPortsAxioms(List<Port> ports, OWLClass block) {
        for (Port port : ports) {

            OWLClass owlPort = getOWLClass(port.getName());
            OWLClass owlParentClass = getOWLClass(port.getSuperClass());
            OWLClass owlPortClass = getOWLClass(portClass);
            OWLClassExpression classExpression = dataFactory.getOWLObjectSomeValuesFrom(getHasPortRelation(), owlPort);

            classAxioms.add(dataFactory.getOWLSubClassOfAxiom(owlParentClass, owlPortClass));
            classAxioms.add(dataFactory.getOWLSubClassOfAxiom(owlPort, owlParentClass));
            classAxioms.add(dataFactory.getOWLSubClassOfAxiom(block, classExpression));
        }
    }

    private void getHasPortAxiom(End end) {
        Port port = parser.getPortMap().get(end.getRole());

        OWLIndividual partWithPort = individuals.get(end.getPartWithPort());
        OWLIndividual portIndividual = dataFactory.getOWLNamedIndividual(port.getName());
        OWLClass individualType = getOWLClass(port.getSuperClass());

        individualAxioms.add(dataFactory.getOWLClassAssertionAxiom(individualType, portIndividual));
        individualAxioms.add(dataFactory.getOWLObjectPropertyAssertionAxiom(getHasPortRelation(), partWithPort, portIndividual));
    }

    private void getBlockConnectorsAxioms(List<OwnedConnector> connectors) {
        for (OwnedConnector connector : connectors) {
            if (connector.getFirstEnd().getPartWithPort() != null && connector.getSecondEnd().getPartWithPort() != null) {

                getHasPortAxiom(connector.getFirstEnd());
                getHasPortAxiom(connector.getSecondEnd());

            } else {

                OWLIndividual firstIndividual = individuals.get(connector.getFirstEnd().getRole());
                OWLIndividual secondIndividual = individuals.get(connector.getSecondEnd().getRole());

                individualAxioms.add(dataFactory.getOWLObjectPropertyAssertionAxiom(getHasPartRelation(), firstIndividual, secondIndividual));
            }
        }
    }

    private OWLClassExpression getOWLClassExpression(Association association, OWLClass owlOwnedClass) {
        OWLClassExpression classExpression;

        if (association.hasExactCardinalityRestriction()) {

            int cardinality = Integer.parseInt(association.getOwned().getLowerValue().getValue());
            classExpression = dataFactory.getOWLObjectExactCardinality(cardinality, getHasPartRelation(), owlOwnedClass);

        } else if (association.hasMinCardinalityRestriction()) {

            int cardinality = Integer.parseInt(association.getOwned().getLowerValue().getValue());
            classExpression = dataFactory.getOWLObjectMinCardinality(cardinality, getHasPartRelation(), owlOwnedClass);

        } else if (association.hasMaxCardinalityRestriction()) {

            int cardinality = Integer.parseInt(association.getOwned().getUpperValue().getValue());
            classExpression = dataFactory.getOWLObjectMaxCardinality(cardinality, getHasPartRelation(), owlOwnedClass);

        } else {

            classExpression = dataFactory.getOWLObjectSomeValuesFrom(getHasPartRelation(), owlOwnedClass);
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

    public Stream<OWLAnnotationAxiom> annotationAxioms() {
        return annotationAxioms.stream();
    }

    public Stream<OWLAxiom> axioms() {
        List<OWLAxiom> axioms = new ArrayList<>();

        axioms.addAll(classAxioms);
        axioms.addAll(objectPropertyAxioms);
        axioms.addAll(dataPropertyAxioms);
        axioms.addAll(individualAxioms);
        axioms.addAll(annotationAxioms);

        return axioms.stream();
    }

    public void saveOntology(File file) throws OWLOntologyStorageException, OWLOntologyCreationException {
        OWLOntology ontology = ontologyManager.createOntology(IRI.create(file.toURI()));
        ontology.addAxioms(axioms());
        ontologyManager.saveOntology(ontology);
    }
}
