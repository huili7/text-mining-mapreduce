package de.hu.wbi.parser.pubmed.impl;

import de.berlin.hu.wbi.common.xml.Context;
import de.berlin.hu.wbi.common.xml.NodeBasedClass;
import de.hu.wbi.parser.pubmed.iface.PubDate;

public class PubDateImpl<N, L> extends NodeBasedClass<N, L> implements PubDate {

	public PubDateImpl(Context<N, L> context, N base) {
		super(context, base);
	}

	@Override
	public boolean isStructured() {
		//if there is a year node it is structured
		N result = evaluateAsNode("Year");
		return result != null;
	}

	@Override
	public String year() {
		return evaluateAsString("Year");
	}

	@Override
	public String month() {
		return evaluateAsString("Month");
	}

	@Override
	public String day() {
		return evaluateAsString("Day");
	}

	@Override
	public String toPlainString() {
		return evaluateAsString("MedlineDate");
	}

}
