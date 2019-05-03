package uni.tukl.cs.cps.revelio.sysML;

import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import uni.tukl.cs.cps.revelio.parser.Enums;

public class OwnedComment extends SysMLNode {

    private String body;

    public OwnedComment(SysMLNode node, NodeList childNodes) {
        this.id = node.id;
        this.name = node.name;
        this.xmiType = node.xmiType;
        this.type = node.type;

        if (type == null) {
            for (int i = 0; i < childNodes.getLength(); i++) {
                Node child = childNodes.item(i);
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
}
