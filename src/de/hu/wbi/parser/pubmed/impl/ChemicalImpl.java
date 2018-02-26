package de.hu.wbi.parser.pubmed.impl;

import de.berlin.hu.wbi.common.xml.NodeBasedClass;
import de.hu.wbi.parser.pubmed.iface.Chemical;

public class ChemicalImpl<N, L> extends NodeBasedClass<N, L> implements Chemical {

	@Override
	public String nameOfSubstance() {
		String nameOfSubstance = evaluateAsString("NameOfSubstance");
		return nameOfSubstance;
	}

	@Override
	public String registryNumber() {
		String registryNumber = evaluateAsString("RegistryNumber");
		return registryNumber;
	}

}
