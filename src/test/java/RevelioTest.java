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
        revelio = new Revelio(sysMLFilePath, "http://www.semanticweb.org/revelio/test-ontology/");
    }

    @After
    public void tearDown() throws Exception {
        File file = new File("resources/output/revelio.owl");
        file.getParentFile().mkdir();
        file.createNewFile();
        revelio.saveOntology(file);
    }

    @Test
    public void GetClassAxiomsTest() {
        assertEquals(28, revelio.classAxioms().count());
    }

    @Test
    public void GetDataPropertyAxiomsTest() {
        assertEquals(3, revelio.dataPropertyAxioms().count());
    }

    @Test
    public void GetIndividualAxiomsTest() {
        assertEquals(26, revelio.individualAxioms().count());
    }

    @Test
    public void GetAnnotationAxiomsTest() {
        assertEquals(20, revelio.annotationAxioms().count());
    }

    @Test
    public void GetTotalAxiomsTest() {
        assertEquals(78, revelio.axioms().count());
    }

    @Test
    public void GetObjectPropertyAxiomsTest() {
        assertEquals(1, revelio.objectPropertyAxioms().count());
    }
}