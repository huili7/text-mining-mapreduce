package de.hu.wbi.parser.pubmed.impl;

import de.hu.wbi.parser.pubmed.iface.Name;


public class NameImpl implements Name {
	
	private String fullName;

	public NameImpl(String foreName, String lastName) {
		fullName = foreName + " " + lastName;
	}

	@Override
	public String fullName() {
		return fullName;
	}

}
