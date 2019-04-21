import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;

public class ReveliOWLParserTest {

    private String sysMLFilePath = "resources/samples/block_diagram.xml";
    private ReveliOWLParser revelio;

    @Before
    public void setUp() throws Exception {
        revelio = new ReveliOWLParser(sysMLFilePath);
    }

    @Test
    public void GetClassAxiomsTest() {
        assertEquals(revelio.GetClassAxioms().size(), 0);
    }
}