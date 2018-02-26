package de.hu.wbi.parser.pubmed.impl;

import de.berlin.hu.wbi.common.xml.NodeBasedClass;
import de.hu.wbi.parser.pubmed.iface.PublicationType;

public class PublicationTypeImpl<N, L> extends NodeBasedClass<N, L> implements PublicationType {
	public String toString() {
		String publicationType = evaluateAsString(".");
		return publicationType;
	}
}
