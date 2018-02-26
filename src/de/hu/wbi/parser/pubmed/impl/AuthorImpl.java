package de.hu.wbi.parser.pubmed.impl;

import de.berlin.hu.wbi.common.xml.NodeBasedClass;
import de.hu.wbi.parser.pubmed.iface.Author;
import de.hu.wbi.parser.pubmed.iface.Name;

public class AuthorImpl<N, L> extends NodeBasedClass<N, L> implements Author {

	@Override
	public Name name() {
		return new NameImpl(foreName(), lastName());
	}
	
	@Override
	public String lastName() {
		return evaluateAsString("LastName");
	}
	
	@Override
	public String foreName() {
		return evaluateAsString("ForeName");
	}
	
	@Override
	public String initials() {
		return evaluateAsString("Initials");
	}

}
