import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import uni.tukl.cs.cps.revelio.Revelio;

import java.io.File;

import static org.junit.Assert.*;

public class RevelioTest {

    private String sysMLFilePath = "resources/samples/block_diagram.xml";
    private Revelio revelio;

    @Before
    public void setUp() throws Exception {
        revelio = new Revelio(sysMLFilePath, "http://www.semanticweb.org/revelio/test-ontology/", "Part");
    }

    @After
    public void tearDown() throws Exception {
        File file = new File("resources/output/revelio.owl");
        file.getParentFile().mkdir();
        file.createNewFile();
        revelio.saveOntology(file);
    }

    @Test
    public void GetClassesTest() {
        revelio.classAxioms();
        assertEquals(revelio.classAxioms().count(), 12);
    }
}