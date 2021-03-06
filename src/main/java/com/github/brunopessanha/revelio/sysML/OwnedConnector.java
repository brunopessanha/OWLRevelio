package com.github.brunopessanha.revelio.sysML;

import org.w3c.dom.Node;
import com.github.brunopessanha.revelio.parser.Enums;

public class OwnedConnector extends SysMLNode {

    private End firstEnd;

    private End secondEnd;

    public OwnedConnector(Node node) {

        this.name = getAttributeValue(node.getAttributes(), Enums.XML_Attribute.Name.toString());
        this.id = getAttributeValue(node.getAttributes(), Enums.XML_Attribute.XMI_ID.toString());

        boolean firstChild = true;
        for (int i = 0; i < node.getChildNodes().getLength(); i++) {
            Node child = node.getChildNodes().item(i);
            if (child.getNodeName() == Enums.XML_Tag.End.toString()) {
                if (firstChild) {
                    this.firstEnd = new End(child);
                    firstChild = false;
                } else {
                    this.secondEnd = new End(child);
                }
            }
        }
    }

    public End getFirstEnd() {
        return firstEnd;
    }

    public void setFirstEnd(End firstEnd) {
        this.firstEnd = firstEnd;
    }

    public End getSecondEnd() {
        return secondEnd;
    }

    public void setSecondEnd(End secondEnd) {
        this.secondEnd = secondEnd;
    }
}
