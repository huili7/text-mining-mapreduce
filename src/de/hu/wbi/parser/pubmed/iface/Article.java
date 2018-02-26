package de.hu.wbi.parser.pubmed.iface;

public interface Article {
	Journal journal();
	Abstract abstrct();
	AuthorList authorList();
	String articleTitleText();
	String affiliation();
	String pagination();
	PublicationType[] publicationTypes();
}
