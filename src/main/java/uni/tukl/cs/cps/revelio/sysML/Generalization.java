package uni.tukl.cs.cps.revelio.sysML;

public class Generalization extends  SysMLNode {

    private String general;

    public Generalization(SysMLNode childNode) {
        this.xmiType = childNode.xmiType;
        this.id = childNode.id;
    }

    public String getGeneral() {
        return general;
    }

    public void setGeneral(String general) {
        this.general = general;
    }
}
