package uni.tukl.cs.cps.revelio.sysML;

import org.semanticweb.owlapi.vocab.OWL2Datatype;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import uni.tukl.cs.cps.revelio.parser.Enums;

public class OwnedAttribute extends SysMLNode {

    private OWL2Datatype dataType;

    public OwnedAttribute(SysMLNode node, NodeList childNodes) {
        this.id = node.id;
        this.name = node.name;
        this.xmiType = node.xmiType;
        this.type = node.type;

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
}
