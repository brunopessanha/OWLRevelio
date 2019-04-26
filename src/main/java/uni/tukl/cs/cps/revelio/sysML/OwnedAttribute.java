package uni.tukl.cs.cps.revelio.sysML;

public class OwnedAttribute extends SysMLNode {

    public OwnedAttribute(SysMLNode node) {
        this.id = node.id;
        this.name = node.name;
        this.xmiType = node.xmiType;
    }

}
