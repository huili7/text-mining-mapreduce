package de.hu.wbi.parser.pubmed.impl;

import de.berlin.hu.wbi.common.xml.Context;
import de.berlin.hu.wbi.common.xml.NodeBasedClass;
import de.hu.wbi.parser.pubmed.iface.JournalIssue;
import de.hu.wbi.parser.pubmed.iface.PubDate;

public class JournalIssueImpl<N, L> extends NodeBasedClass<N, L> implements JournalIssue {

	public JournalIssueImpl(Context<N, L> context, N base) {
		super(context, base);
	}

	@Override
	public PubDate pubDate() {
		N base = evaluateAsNode("PubDate");
		return new PubDateImpl<N, L>(context, base);
	}

	@Override
	public String volume() {
		return evaluateAsString("Volume");
	}

	@Override
	public String issue() {
		return evaluateAsString("Issue");
	}

}
