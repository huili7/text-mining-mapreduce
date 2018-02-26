package de.hu.wbi.parser.pubmed.iface;

public interface ELocationID {
	boolean isValid();
	String getID();
	boolean isDOI();
	boolean isPII();
}
