package uni.tukl.cs.cps.revelio.sysML;

import java.util.ArrayList;
import java.util.List;

public class Block extends SysMLNode {

    private List<OwnedAttribute> attributes;

    private String superClass;

    public Block(String id, String superClass) {
        this.id = id;
        this.superClass = superClass;
        this.attributes = new ArrayList<>();
    }

    public List<OwnedAttribute> getAttributes() {
        return attributes;
    }

    public void setAttributes(List<OwnedAttribute> attributes) {
        this.attributes = attributes;
    }

    public String getSuperClass() {
        return superClass;
    }

    public void setSuperClass(String superClass) {
        this.superClass = superClass;
    }
}
