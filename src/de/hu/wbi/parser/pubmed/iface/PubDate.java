package de.hu.wbi.parser.pubmed.iface;

public interface PubDate {
	boolean isStructured();
	String year();
	String month();
	String day();
	String toPlainString();
}
