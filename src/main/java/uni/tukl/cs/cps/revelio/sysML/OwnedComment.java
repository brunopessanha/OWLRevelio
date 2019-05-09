package uni.tukl.cs.cps.revelio.sysML;

import org.w3c.dom.Node;
import uni.tukl.cs.cps.revelio.parser.Enums;

public class OwnedComment extends SysMLNode {

    private String body;

    private String annotatedElement;

    public OwnedComment(Node node) {
        this.id = getAttributeValue(node.getAttributes(), Enums.XML_Attribute.XMI_ID.toString());
        this.name = getAttributeValue(node.getAttributes(), Enums.XML_Attribute.Name.toString());
        this.type = getAttributeValue(node.getAttributes(), Enums.XML_Attribute.Type.toString());
        this.xmiType = getAttributeValue(node.getAttributes(), Enums.XML_Attribute.XMI_Type.toString());
        this.annotatedElement = getAttributeValue(node.getAttributes(), Enums.XML_Attribute.AnnotatedElement.toString());

        if (type == null) {
            for (int i = 0; i < node.getChildNodes().getLength(); i++) {
                Node child = node.getChildNodes().item(i);
                if (child.getNodeName() == Enums.XML_Tag.Body.toString()) {
                    this.body = child.getTextContent();
                    break;
                }
            }
        }
    }

    public String getBody() {
        return body;
    }

    public void setBody(String body) {
        this.body = body;
    }

    public String getAnnotatedElement() {
        return annotatedElement;
    }

    public void setAnnotatedElement(String annotatedElement) {
        this.annotatedElement = annotatedElement;
    }
}
