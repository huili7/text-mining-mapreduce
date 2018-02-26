package de.hu.wbi.parser.pubmed;

import org.xml.sax.InputSource;


public interface MedlineParser {
	void setHandler(MedlineHandler indexer);
	void parse(InputSource inSource);
}
