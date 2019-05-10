package uni.tukl.cs.cps.revelio.sysML;

import org.semanticweb.owlapi.vocab.OWL2Datatype;
import org.w3c.dom.Node;
import uni.tukl.cs.cps.revelio.parser.Enums;

import java.util.ArrayList;
import java.util.List;

public class OwnedAttribute extends SysMLNode {

    private OWL2Datatype dataType;

    private List<OwnedComment> comments;

    private String association;

    public OwnedAttribute(Node node) {
        this.id = getAttributeValue(node.getAttributes(), Enums.XML_Attribute.XMI_ID.toString());
        this.name = getAttributeValue(node.getAttributes(), Enums.XML_Attribute.Name.toString());
        this.type = getAttributeValue(node.getAttributes(), Enums.XML_Attribute.Type.toString());
        this.xmiType = getAttributeValue(node.getAttributes(), Enums.XML_Attribute.XMI_Type.toString());
        this.association = getAttributeValue(node.getAttributes(), Enums.XML_Attribute.Association.toString());
        this.comments = new ArrayList<>();

        if (type == null) {
            for (int i = 0; i < node.getChildNodes().getLength(); i++) {
                Node child = node.getChildNodes().item(i);
                if (child.getNodeName() == Enums.XML_Tag.Type.toString()) {
                    String composedDataType = getAttributeValue(child.getAttributes(), Enums.XML_Attribute.Href.toString());
                    if (composedDataType != null) {
                        if (composedDataType.contains(Enums.DataType.Integer.toString())) {
                            this.dataType = OWL2Datatype.XSD_INTEGER;
                        } else if (composedDataType.contains(Enums.DataType.Real.toString()) || composedDataType.contains(Enums.DataType.Number.toString())) {
                            this.dataType = OWL2Datatype.XSD_FLOAT;
                        } else {
                            this.dataType = OWL2Datatype.XSD_STRING;
                        }
                        break;
                    }
                }
            }
        }
    }

    public OWL2Datatype getDataType() {
        return dataType;
    }

    public void setDataType(OWL2Datatype dataType) {
        this.dataType = dataType;
    }

    public List<OwnedComment> getComments() {
        return comments;
    }

    public String getAssociation() {
        return association;
    }

    public void setAssociation(String association) {
        this.association = association;
    }
}
