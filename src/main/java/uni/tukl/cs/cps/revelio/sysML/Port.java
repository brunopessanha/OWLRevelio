package uni.tukl.cs.cps.revelio.sysML;

import org.semanticweb.owlapi.vocab.OWL2Datatype;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import uni.tukl.cs.cps.revelio.parser.Enums;

public class Port extends SysMLNode {

    private OWL2Datatype dataType;

    public Port(String port) {
        this.id = port;
    }

    public OWL2Datatype getDataType() {
        return dataType;
    }

    public void setDataType(NodeList childNodes) {
        for (int i = 0; i < childNodes.getLength(); i++) {
            Node child = childNodes.item(i);
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

    public String getSuperClass() {
        return "Port";
    }
}