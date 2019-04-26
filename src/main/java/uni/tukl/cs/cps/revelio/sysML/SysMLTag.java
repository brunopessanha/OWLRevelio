package uni.tukl.cs.cps.revelio.sysML;

public class SysMLTag  {

    private String baseClass;

    private String tagName;

    public SysMLTag (String tagName, String baseClass) {
        this.tagName = tagName;
        this.baseClass = baseClass;
    }

    public String getBaseClass() {
        return baseClass;
    }

    public void setBaseClass(String baseClass) {
        this.baseClass = baseClass;
    }

    public String getTagName() {
        return tagName;
    }

    public void setTagName(String tagName) {
        this.tagName = tagName;
    }
}
