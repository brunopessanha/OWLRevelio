package com.github.brunopessanha.revelio.owl;

import com.github.brunopessanha.revelio.parser.Enums;
import com.github.brunopessanha.revelio.parser.ISysMLParser;
import com.github.brunopessanha.revelio.parser.Util;
import com.github.brunopessanha.revelio.settings.RevelioSettings;
import com.github.brunopessanha.revelio.sysML.*;
import org.semanticweb.owlapi.apibinding.OWLManager;
import org.semanticweb.owlapi.model.*;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Stream;

public class OntologyCreator implements IOntologyCreator {

    private final OWLOntologyManager ontologyManager;
    private final OWLDataFactory dataFactory;

    private final List<OWLClassAxiom> classAxioms;
    private final List<OWLObjectPropertyAxiom> objectPropertyAxioms;
    private final List<OWLDataPropertyAxiom> dataPropertyAxioms;
    private final List<OWLIndividualAxiom> individualAxioms;
    private final List<OWLAnnotationAxiom> annotationAxioms;

    private final Map<String, OWLIndividual> individuals;

    private final RevelioSettings settings;

    private final ISysMLParser parser;

    public OntologyCreator(RevelioSettings settings, ISysMLParser parser) {
        this.ontologyManager = OWLManager.createOWLOntologyManager();
        this.settings = settings;
        this.parser = parser;
        this.classAxioms = new ArrayList<>();
        this.objectPropertyAxioms = new ArrayList<>();
        this.dataPropertyAxioms = new ArrayList<>();
        this.individualAxioms = new ArrayList<>();
        this.annotationAxioms = new ArrayList<>();
        this.individuals = new HashMap<>();
        this.dataFactory = ontologyManager.getOWLDataFactory();
        this.objectPropertyAxioms.add(dataFactory.getOWLSymmetricObjectPropertyAxiom(getIsConnectedToRelation()));
    }

    private OWLObjectProperty getHasPartRelation() {
        return dataFactory.getOWLObjectProperty(getIRI(settings.getHasPartObjectProperty()));
    }

    private OWLObjectProperty getHasPortRelation() {
        return dataFactory.getOWLObjectProperty(getIRI(Enums.Relation.HasPort.toString()));
    }

    private OWLObjectProperty getIsConnectedToRelation() {
        return dataFactory.getOWLObjectProperty(getIRI(Enums.Relation.IsConnectedTo.toString()));
    }

    private IRI getDataPropertyIRI(String name) {
        return this.getIRI(Util.trimAll(name));
    }

    private IRI getIRI(String name) {
        return IRI.create(settings.getOntologyIRI(), Util.trimAll(name));
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

    private OWLAnnotationAxiom getLabelAxiom(String label, IRI iri) {

        OWLLiteral literal = dataFactory.getOWLLiteral(label);
        OWLAnnotationProperty labelAnnotation = dataFactory.getRDFSLabel();

        OWLAnnotation annotation = dataFactory.getOWLAnnotation(labelAnnotation, literal);
        OWLAnnotationAxiom labelAxiom = dataFactory.getOWLAnnotationAssertionAxiom(iri, annotation);

        return labelAxiom;
    }

    public void generateAxioms() {
        for (Block block : parser.getBlockMap().values()) {

            OWLClass owlBlockClass = getOWLClass(Util.trimAll(block.getName()));
            annotationAxioms.add(getLabelAxiom(block.getName(), owlBlockClass.getIRI()));

            OWLClass owlParentClass = getOWLClass(block.getSuperClass());

            OWLClassAxiom axiom = dataFactory.getOWLSubClassOfAxiom(owlBlockClass, owlParentClass);

            classAxioms.add(axiom);

            addCommentsAxioms(block.getComments(), owlBlockClass.getIRI());

            addBlockAttributesAxioms(block.getAttributes(), owlBlockClass);

            addBlockPortsAxioms(block.getPorts(), owlBlockClass);

            addBlockConnectorsAxioms(block.getConnectors());
        }

        for (Association association : parser.getAssociations()) {

            OWLClass owlOwnerClass = getOWLClass(parser.getBlockMap().get(association.getOwner().getType()).getName());
            OWLClass owlOwnedClass = getOWLClass(parser.getBlockMap().get(association.getOwned().getType()).getName());

            OWLClassExpression classExpression = getOWLClassExpression(association, owlOwnedClass);

            OWLClassAxiom axiom = dataFactory.getOWLSubClassOfAxiom(owlOwnerClass, classExpression);

            classAxioms.add(axiom);
        }
    }

    private void addCommentsAxioms(List<OwnedComment> comments, IRI iri) {
        for (OwnedComment comment : comments) {
            annotationAxioms.add(getCommentAxiom(comment.getBody(), iri));
        }
    }

    private void addBlockAttributesAxioms(List<OwnedAttribute> attributes, OWLClass blockClass) {
        for (OwnedAttribute attribute : attributes) {
            if (attribute.getDataType() != null) { // data property attribute

                addDataProperty(blockClass, attribute);

            } else { //instance attribute of internal block diagram
                OWLNamedIndividual individual = dataFactory.getOWLNamedIndividual(getIRI(attribute.getName()));
                individuals.put(attribute.getId(), individual);

                if (parser.getBlockMap().containsKey(attribute.getType())) {
                    OWLClass individualType = getOWLClass(parser.getBlockMap().get(attribute.getType()).getName());
                    individualAxioms.add(dataFactory.getOWLClassAssertionAxiom(individualType, individual));
                }

                addCommentsAxioms(attribute.getComments(), individual.getIRI());
            }
        }
    }

    private void addDataProperty(OWLClass blockClass, OwnedAttribute attribute) {
        OWLDataProperty dataProperty = dataFactory.getOWLDataProperty(getDataPropertyIRI(attribute.getName()));
        annotationAxioms.add(getLabelAxiom(attribute.getName(), dataProperty.getIRI()));

        OWLDataPropertyRangeAxiom rangeAxiom = dataFactory.getOWLDataPropertyRangeAxiom(dataProperty, attribute.getDataType().getDatatype(dataFactory));

        dataPropertyAxioms.add(rangeAxiom);

        OWLClassExpression classExpression = dataFactory.getOWLDataSomeValuesFrom(dataProperty, attribute.getDataType());
        OWLClassAxiom dataPropertyRestrictionAxiom = dataFactory.getOWLSubClassOfAxiom(blockClass, classExpression);

        classAxioms.add(dataPropertyRestrictionAxiom);
    }

    private void addBlockPortsAxioms(List<Port> ports, OWLClass block) {
        for (Port port : ports) {
            OWLClass owlParentClass = getOWLClass(port.getSuperClass().toString());
            OWLClass owlPortClass = getOWLClass(settings.getPortClass());
            OWLClassExpression classExpression = dataFactory.getOWLObjectSomeValuesFrom(getHasPortRelation(), owlPortClass);

            getPortIndividual(port);

            classAxioms.add(dataFactory.getOWLSubClassOfAxiom(owlParentClass, owlPortClass));
            classAxioms.add(dataFactory.getOWLSubClassOfAxiom(block, classExpression));
        }
    }

    private OWLIndividual addHasPortAxiom(End end) {
        Port port = parser.getPortMap().get(end.getRole());

        OWLIndividual partWithPort = individuals.get(end.getPartWithPort());
        OWLIndividual portIndividual = getPortIndividual(port);

        individualAxioms.add(dataFactory.getOWLObjectPropertyAssertionAxiom(getHasPortRelation(), partWithPort, portIndividual));

        return portIndividual;
    }

    private OWLIndividual getPortIndividual(Port port) {

        if (!individuals.containsKey(port.getId())) {

            OWLNamedIndividual portIndividual = dataFactory.getOWLNamedIndividual(Util.trimAll(port.getName()));
            annotationAxioms.add(getLabelAxiom(port.getName(), portIndividual.getIRI()));
            individuals.put(port.getId(), portIndividual);

            OWLClass portType = getOWLClass(port.getSuperClass().toString());

            if (port.getSuperClass() == Enums.Port.FlowPort) {
                OWLClass parentPortType = portType;

                if (Enums.FlowPortDirection.In.toString().equals(port.getDirection())) {
                    portType = getOWLClass(Enums.FlowPort.InputFlowPort.toString());
                } else if (Enums.FlowPortDirection.Out.toString().equals(port.getDirection())) {
                    portType = getOWLClass(Enums.FlowPort.OutputFlowPort.toString());
                } else {
                    portType = getOWLClass(Enums.FlowPort.InputOutputFlowPort.toString());
                }

                classAxioms.add(dataFactory.getOWLSubClassOfAxiom(portType, parentPortType));
            }

            individualAxioms.add(dataFactory.getOWLClassAssertionAxiom(portType, portIndividual));
        }

        return individuals.get(port.getId());
    }

    private void addBlockConnectorsAxioms(List<OwnedConnector> connectors) {
        for (OwnedConnector connector : connectors) {

            OWLIndividual connection = dataFactory.getOWLNamedIndividual(getConnectionName(connector));
            individuals.put(connector.getId(), connection);

            if (connector.getFirstEnd().getPartWithPort() != null) {
                OWLIndividual firstPort = addHasPortAxiom(connector.getFirstEnd());
                individualAxioms.add(dataFactory.getOWLObjectPropertyAssertionAxiom(getIsConnectedToRelation(), connection, firstPort));
            } else {
                OWLIndividual firstIndividual = individuals.get(connector.getFirstEnd().getRole());
                individualAxioms.add(dataFactory.getOWLObjectPropertyAssertionAxiom(getIsConnectedToRelation(), connection, firstIndividual));
            }

            if (connector.getSecondEnd().getPartWithPort() != null) {
                OWLIndividual secondPort = addHasPortAxiom(connector.getSecondEnd());
                individualAxioms.add(dataFactory.getOWLObjectPropertyAssertionAxiom(getIsConnectedToRelation(), connection, secondPort));
            } else {
                OWLIndividual secondIndividual = individuals.get(connector.getSecondEnd().getRole());
                individualAxioms.add(dataFactory.getOWLObjectPropertyAssertionAxiom(getIsConnectedToRelation(), connection, secondIndividual));
            }

            individualAxioms.add(dataFactory.getOWLClassAssertionAxiom(getOWLClass(settings.getConnectionClass()), connection));
        }
    }

    private String getConnectionName(OwnedConnector connector) {
        return connector.getName() +  "_" + parser.getPortMap().get(connector.getFirstEnd().getRole()).getName()
                + "_" + parser.getPortMap().get(connector.getSecondEnd().getRole()).getName();
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
