package de.hu.wbi.parser.pubmed.impl;

import de.berlin.hu.wbi.common.xml.Context;
import de.berlin.hu.wbi.common.xml.NodeBasedClass;
import de.hu.wbi.parser.pubmed.iface.ELocationID;

public class ELocationIDImpl<N, L> extends NodeBasedClass<N, L> implements ELocationID {

	public ELocationIDImpl(Context<N, L> context, N base) {
		super(context, base);
	}

	@Override
	public boolean isValid() {
		boolean value = evaluateAsBoolean("@ValidYN='Y'");
		return value;
	}

	@Override
	public String getID() {
		String id = evaluateAsString(".");
		return id;
	}

	@Override
	public boolean isDOI() {
		boolean value = evaluateAsBoolean("@EIdType='doi'");
		return value;
	}

	@Override
	public boolean isPII() {
		boolean value = evaluateAsBoolean("@EIdType='pii'");
		return value;
	}

}
