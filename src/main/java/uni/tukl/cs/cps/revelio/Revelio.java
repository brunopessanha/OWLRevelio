package uni.tukl.cs.cps.revelio;

import org.semanticweb.owlapi.model.*;
import org.w3c.dom.Document;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;
import uni.tukl.cs.cps.revelio.Exceptions.InvalidSysMLFileException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Revelio implements SysML2OWLParser {

    private String filePath;

    private List<OWLClassAxiom> classAxioms;
    private List<OWLObjectPropertyAxiom> objectPropertyAxioms;
    private List<OWLDataPropertyAxiom> dataPropertyAxioms;
    private List<OWLIndividualAxiom> individualAxioms;

    public Revelio(String filePath) throws InvalidSysMLFileException {
        this.filePath = filePath;
        this.classAxioms = new ArrayList<>();
        this.objectPropertyAxioms = new ArrayList<>();
        this.dataPropertyAxioms = new ArrayList<>();
        this.individualAxioms = new ArrayList<>();

        Document doc = null;

        try {
            File file = new File(filePath);
            DocumentBuilder dBuilder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
            doc = dBuilder.parse(file);
        } catch (ParserConfigurationException pcEx) {
            throw new InvalidSysMLFileException(pcEx);
        } catch (IOException ioEx) {
            throw new InvalidSysMLFileException(ioEx);
        } catch (SAXException saxEx) {
            throw new InvalidSysMLFileException(saxEx);
        }

        if (doc != null) {

            if(!doc.getDocumentElement().getNodeName().equals(Enums.XML_Tag.XMI.toString())) {
                throw new InvalidSysMLFileException("The file provided is not a valid XMI file.");
            }

            parseSysMLBlockDiagram(doc);
        }
    }

    private void parseSysMLBlockDiagram(Document doc) {

        Map<String, SysMLBlock> blockMap = new HashMap<>();
        NodeList nodeBlocks = doc.getElementsByTagName(Enums.XML_Tag.BlockDiagram.toString());
        for (int i = 0; i < nodeBlocks.getLength(); i++) {
            SysMLBlock block = new SysMLBlock();
            block.setId(nodeBlocks.item(i).getAttributes().getNamedItem(Enums.XML_Attribute.BaseClass.toString()).getNodeValue());
            blockMap.put(block.getId(), block);
        }

        NodeList packagedElements = doc.getElementsByTagName(Enums.XML_Tag.PackagedElement.toString());
        for (int i = 0; i < packagedElements.getLength(); i++) {

            NamedNodeMap attributes = packagedElements.item(i).getAttributes();
            String id = attributes.getNamedItem(Enums.XML_Attribute.XMI_ID.toString()).getNodeValue();
            String type = attributes.getNamedItem(Enums.XML_Attribute.XMI_Type.toString()).getNodeValue();
            String name = attributes.getNamedItem(Enums.XML_Attribute.Name.toString()).getNodeValue();

            if (type.equals(Enums.XMI_Type.UML_Class.toString()) && blockMap.containsKey(id)) {

                SysMLBlock block = blockMap.get(id);
                block.setName(name);
                System.out.println("Block Name: " + block.getName());

            } else if (type.equals(Enums.XMI_Type.UML_Association.toString())) {

                NodeList ownedEnds = packagedElements.item(i).getChildNodes();

                boolean foundPart = false;
                String partOfId = "";
                String aggregation = "";
                String partId = "";

                for (int j = 0; j < ownedEnds.getLength(); j++) {
                    if (ownedEnds.item(j).getNodeName().equals(Enums.XML_Tag.OwnedEnd.toString())) {
                        if (!foundPart) {
                            partId = ownedEnds.item(j).getAttributes().getNamedItem(Enums.XML_Attribute.Type.toString()).getNodeValue();
                            aggregation = ownedEnds.item(j).getAttributes().getNamedItem(Enums.XML_Attribute.Aggregation.toString()).getNodeValue();
                            foundPart = true;
                        } else {
                            partOfId = ownedEnds.item(j).getAttributes().getNamedItem(Enums.XML_Attribute.Type.toString()).getNodeValue();
                            break;
                        }
                    }
                }

                System.out.println("Association (" + aggregation + "): " + name + " -> " + blockMap.get(partOfId).getName() + " hasPart " + blockMap.get(partId).getName());
            }
        }
    }

    @Override
    public List<OWLClassAxiom> GetClassAxioms() {
        return classAxioms;
    }

    @Override
    public List<OWLObjectPropertyAxiom> GetObjectPropertyAxioms() {
        return objectPropertyAxioms;
    }

    @Override
    public List<OWLDataPropertyAxiom> GetDataPropertyAxioms() {
        return dataPropertyAxioms;
    }

    @Override
    public List<OWLIndividualAxiom> GetIndividualAxioms() {
        return individualAxioms;
    }
}
