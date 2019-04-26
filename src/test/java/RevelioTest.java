import org.junit.Before;
import org.junit.Test;
import uni.tukl.cs.cps.revelio.Revelio;

import static org.junit.Assert.*;

public class RevelioTest {

    private String sysMLFilePath = "resources/samples/block_diagram.xml";
    private Revelio revelio;

    @Before
    public void setUp() throws Exception {
        revelio = new Revelio(sysMLFilePath, "http://www.semanticweb.org/revelio/test-ontology", "Part");
    }

    @Test
    public void GetClassesTest() {
        assertEquals(revelio.GetClasses().size(), 7);
    }
}