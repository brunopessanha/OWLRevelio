package uni.tukl.cs.cps.revelio.sysML;

public class Association extends SysMLNode {

    private OwnedEnd owner;

    private OwnedEnd owned;

    public Association() {

    }

    public Association(PackagedElement packagedElement, OwnedEnd owner, OwnedEnd owned) {
        this.id = packagedElement.getId();
        this.name = packagedElement.getName();
        this.owner = owner;
        this.owned = owned;
    }

    public OwnedEnd getOwner() {
        return owner;
    }

    public void setOwner(OwnedEnd owner) {
        this.owner = owner;
    }

    public OwnedEnd getOwned() {
        return owned;
    }

    public void setOwned(OwnedEnd owned) {
        this.owned = owned;
    }
}
