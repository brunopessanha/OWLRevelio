package uni.tukl.cs.cps.revelio;

public class SysMLBlock {

    private String id;

    private String name;


    public SysMLBlock(String id, String name) {
        this.id = id;
        this.name = name;
    }

    public SysMLBlock() { }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
