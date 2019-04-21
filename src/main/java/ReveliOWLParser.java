import org.semanticweb.owlapi.model.*;
import org.w3c.dom.Document;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class ReveliOWLParser implements SysML2OWLParser {

    private String filePath;
    private List<OWLClassAxiom> classAxioms;
    private List<OWLObjectPropertyAxiom> objectPropertyAxioms;
    private List<OWLDataPropertyAxiom> dataPropertyAxioms;
    private List<OWLIndividualAxiom> individualAxioms;

    public ReveliOWLParser(String filePath) throws ParserConfigurationException, IOException, SAXException {
        this.filePath = filePath;
        this.classAxioms = new ArrayList<>();
        this.objectPropertyAxioms = new ArrayList<>();
        this.dataPropertyAxioms = new ArrayList<>();
        this.individualAxioms = new ArrayList<>();

        File file = new File(filePath);
        DocumentBuilder dBuilder = DocumentBuilderFactory.newInstance().newDocumentBuilder();

        Document doc = dBuilder.parse(file);
        System.out.println("Root element :" + doc.getDocumentElement().getNodeName());
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
