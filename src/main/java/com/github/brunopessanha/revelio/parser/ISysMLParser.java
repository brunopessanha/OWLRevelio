package com.github.brunopessanha.revelio.parser;

import com.github.brunopessanha.revelio.sysML.Association;
import com.github.brunopessanha.revelio.sysML.Block;
import com.github.brunopessanha.revelio.sysML.Port;

import java.util.List;
import java.util.Map;

public interface ISysMLParser {

    Map<String, Block> getBlockMap();

    Map<String, Port> getPortMap();

    List<Association> getAssociations();

    void parse();
}
