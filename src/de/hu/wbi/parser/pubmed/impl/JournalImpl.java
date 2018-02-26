package de.hu.wbi.parser.pubmed.impl;

import de.berlin.hu.wbi.common.xml.Context;
import de.berlin.hu.wbi.common.xml.NodeBasedClass;
import de.hu.wbi.parser.pubmed.iface.Journal;
import de.hu.wbi.parser.pubmed.iface.JournalIssue;

public class JournalImpl<N, L> extends NodeBasedClass<N, L> implements Journal {

	public JournalImpl(Context<N, L> context, N base) {
		super(context, base);
	}

	@Override
	public JournalIssue journalIssue() {
		N base = evaluateAsNode("JournalIssue");
		return new JournalIssueImpl<N, L>(context, base);
	}

	@Override
	public String title() {
		return evaluateAsString("Title");
	}

}
