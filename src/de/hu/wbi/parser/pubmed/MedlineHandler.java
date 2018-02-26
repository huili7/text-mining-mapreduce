package de.hu.wbi.parser.pubmed;

import de.hu.wbi.parser.pubmed.iface.MedlineCitation;


public interface MedlineHandler {
	void handle(MedlineCitation citation);
}
