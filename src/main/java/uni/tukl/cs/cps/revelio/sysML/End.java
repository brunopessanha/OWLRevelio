package uni.tukl.cs.cps.revelio.sysML;

import org.w3c.dom.Node;
import uni.tukl.cs.cps.revelio.parser.Enums;

public class End extends SysMLNode {

    private String partWithPort;

    private String role;

    End(Node node) {
        this.id = getAttributeValue(node.getAttributes(), Enums.XML_Attribute.XMI_ID.toString());
        this.name = getAttributeValue(node.getAttributes(), Enums.XML_Attribute.Name.toString());
        this.xmiType = getAttributeValue(node.getAttributes(), Enums.XML_Attribute.XMI_Type.toString());
        this.partWithPort = getAttributeValue(node.getAttributes(), Enums.XML_Attribute.PartWithPort.toString());
        this.role = getAttributeValue(node.getAttributes(), Enums.XML_Attribute.Role.toString());
    }

    public String getPartWithPort() {
        return partWithPort;
    }

    public void setPartWithPort(String partWithPort) {
        this.partWithPort = partWithPort;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }
}
