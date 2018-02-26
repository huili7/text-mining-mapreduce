package de.hu.wbi.parser.pubmed.iface;


public interface MedlineCitation {
	MeshHeading[] meshHeadings();
	Chemical[] chemicals();
	String pmid();
	String[] geneSymbols();
	Article article();
	ELocationID eLocationId();
}
