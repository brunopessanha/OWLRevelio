package uni.tukl.cs.cps.revelio.parser;

import uni.tukl.cs.cps.revelio.sysML.Association;
import uni.tukl.cs.cps.revelio.sysML.Block;
import uni.tukl.cs.cps.revelio.sysML.Port;

import java.util.List;
import java.util.Map;

public interface ISysMLParser {

    Map<String, Block> getBlockMap();

    Map<String, Port> getPortMap();

    List<Association> getAssociations();
}
