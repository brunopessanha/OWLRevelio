package uni.tukl.cs.cps.revelio;

import org.semanticweb.owlapi.apibinding.OWLManager;
import org.semanticweb.owlapi.model.*;
import org.w3c.dom.Document;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;
import uni.tukl.cs.cps.revelio.exceptions.InvalidSysMLFileException;
import uni.tukl.cs.cps.revelio.sysML.*;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class Revelio implements SysML2OWLParser {

    private final OWLDataFactory dataFactory;
    private final OWLOntologyManager ontologyManager;

    private String ontologyPrefix;
    private Map<String, Block> blockMap;
    private Map<String, ParticipantProperty> propertyMap;
    private List<Association> associations;

    private List<OWLClass> classes;
    private List<OWLObjectProperty> objectProperties;
    private List<OWLDataProperty> dataProperties;
    private List<OWLIndividual> individuals;

    public Revelio(String filePath, String ontologyPrefix) throws InvalidSysMLFileException {

        this.ontologyManager = OWLManager.createOWLOntologyManager();
        this.dataFactory = ontologyManager.getOWLDataFactory();
        this.ontologyPrefix = ontologyPrefix;
        this.associations = new ArrayList<>();

        Document doc = null;

        try {
            File file = new File(filePath);
            DocumentBuilder dBuilder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
            doc = dBuilder.parse(file);
            if(!doc.getDocumentElement().getNodeName().equals(Enums.XML_Tag.XMI.toString())) {
                throw new InvalidSysMLFileException("The file provided is not a valid XMI file.");
            }
            parseBlockDiagram(doc);

        } catch (ParserConfigurationException | IOException | SAXException ex) {
            throw new InvalidSysMLFileException(ex);
        }
    }

    private void parseBlockDiagram(Document doc) {

        blockMap = parseNodesByTag(doc, Enums.XML_Tag.BlockDiagram.toString()).stream()
                .map(t -> new Block(t.getBase())).collect(Collectors.toMap(Block::getId, block -> block));

        propertyMap = parseNodesByTag(doc, Enums.XML_Tag.ParticipantProperty.toString()).stream()
                .map(t -> new ParticipantProperty(t.getBase())).collect(Collectors.toMap(ParticipantProperty::getId, property -> property));

        NodeList packagedElements = doc.getElementsByTagName(Enums.XML_Tag.PackagedElement.toString());
        for (int i = 0; i < packagedElements.getLength(); i++) {

            PackagedElement packagedElement = new PackagedElement(packagedElements.item(i).getAttributes(), packagedElements.item(i).getChildNodes());

            if (packagedElement.getType().equals(Enums.XMI_Type.UML_Class.toString()) && blockMap.containsKey(packagedElement.getId())) {
                parseBlock(packagedElement);
            } else if (packagedElement.getType().equals(Enums.XMI_Type.UML_Association.toString())) {
                associations.addAll(parseAssociation(packagedElement));
            }
        }
    }

    private void parseBlock(PackagedElement packagedElement) {
        Block block = blockMap.get(packagedElement.getId());
        block.setName(packagedElement.getName());

        for (int i = 0; i < packagedElement.getChildNodes().getLength(); i++) {
            OwnedAttribute attribute = new OwnedAttribute(packagedElement.getChildNodes().item(i).getAttributes());
            if (attribute.getXmiType() != null && attribute.getXmiType().equals(Enums.XMI_Type.UML_Property.toString())) {
                ParticipantProperty property = propertyMap.get(attribute.getId());
                property.setName(attribute.getName());
                block.getAttributes().add(attribute);
            }
        }

        System.out.println("Block Name: " + block.getName());
    }

    private List<SysMLTag> parseNodesByTag(Document doc, String tagName) {
        NodeList nodeBlocks = doc.getElementsByTagName(tagName);
        List<SysMLTag> tags = new ArrayList<>();
        for (int i = 0; i < nodeBlocks.getLength(); i++) {
            SysMLTag tag = null;
            if (tagName.equals(Enums.XML_Tag.BlockDiagram.toString())) {
                tag = new SysMLTag(tagName, nodeBlocks.item(i).getAttributes().getNamedItem(Enums.XML_Attribute.BaseClass.toString()).getNodeValue());
            } else if (tagName.equals(Enums.XML_Tag.ParticipantProperty.toString())) {
                tag = new SysMLTag(tagName, nodeBlocks.item(i).getAttributes().getNamedItem(Enums.XML_Attribute.BaseProperty.toString()).getNodeValue());
            }
            if (tag != null) {
                tags.add(tag);
            }
        }
        return tags;
    }

    private List<Association> parseAssociation(PackagedElement packagedElement) {
        OwnedEnd owner = null;
        OwnedEnd owned = null;

        List<Association> umlAssociations = new ArrayList<>();

        boolean foundPart = false;
        for (int i = 0; i < packagedElement.getChildNodes().getLength() && owner == null; i++) {
            if (packagedElement.getChildNodes().item(i).getNodeName().equals(Enums.XML_Tag.OwnedEnd.toString())) {
                if (!foundPart) {
                    foundPart = true;
                    owned = new OwnedEnd(packagedElement.getChildNodes().item(i).getAttributes());
                } else {
                    owner = new OwnedEnd(packagedElement.getChildNodes().item(i).getAttributes());
                    Association association = new Association(packagedElement, owner, owned);
                    umlAssociations.add(association);

                    System.out.println("Association (" + association.getOwned().getAggregation() + "): " + association.getName() + " -> "
                            + blockMap.get(association.getOwner().getType()).getName() + " hasPart " + blockMap.get(association.getOwned().getType()).getName());
                }
            }
        }

        return umlAssociations;
    }

    @Override
    public List<OWLClass> GetClasses() {
        if (classes == null) {
            classes = new ArrayList<>();
            for (Block block : blockMap.values()) {
                IRI iri = IRI.create(ontologyPrefix, block.getName());
                OWLClass owlClass = dataFactory.getOWLClass(iri);
                classes.add(owlClass);
            }
        }
        return classes;
    }

    @Override
    public List<OWLObjectProperty> GetObjectProperties() {
        return objectProperties;
    }

    @Override
    public List<OWLDataProperty> GetDataProperties() {
        return dataProperties;
    }

    @Override
    public List<OWLIndividual> GetIndividuals() {
        return individuals;
    }
}
