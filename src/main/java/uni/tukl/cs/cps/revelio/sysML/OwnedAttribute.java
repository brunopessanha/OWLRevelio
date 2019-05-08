package uni.tukl.cs.cps.revelio.sysML;

import org.semanticweb.owlapi.vocab.OWL2Datatype;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import uni.tukl.cs.cps.revelio.parser.Enums;

import java.util.ArrayList;
import java.util.List;

public class OwnedAttribute extends SysMLNode {

    private OWL2Datatype dataType;

    private List<OwnedComment> comments;

    public OwnedAttribute(SysMLNode node, NodeList childNodes) {
        this.id = node.id;
        this.name = node.name;
        this.xmiType = node.xmiType;
        this.type = node.type;
        this.comments = new ArrayList<>();

        if (type == null) {
            for (int i = 0; i < childNodes.getLength(); i++) {
                Node child = childNodes.item(i);
                if (child.getNodeName() == Enums.XML_Tag.Type.toString()) {
                    String composedDataType = getAttributeValue(child.getAttributes(), Enums.XML_Attribute.Href.toString());
                    if (composedDataType != null) {
                        if (composedDataType.contains("Integer")) {
                            this.dataType = OWL2Datatype.XSD_INTEGER;
                        } else if (composedDataType.contains("Real") || composedDataType.contains("Number")) {
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
}
