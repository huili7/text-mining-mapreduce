package de.hu.wbi.parser.pubmed.impl;

import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpression;

import de.berlin.hu.wbi.common.xml.NodeBasedClass;
import de.hu.wbi.parser.pubmed.iface.Article;
import de.hu.wbi.parser.pubmed.iface.Chemical;
import de.hu.wbi.parser.pubmed.iface.ELocationID;
import de.hu.wbi.parser.pubmed.iface.MedlineCitation;
import de.hu.wbi.parser.pubmed.iface.MeshHeading;

public class MedlineCitationImpl<N, L> extends NodeBasedClass<N, L> implements MedlineCitation {

	@Override
	public MeshHeading[] meshHeadings() {
		try {
			L nodes = evaluateAsNodeSet("./MeshHeadingList/MeshHeading");
			MeshHeading[] array = new MeshHeadingImpl[context.size(nodes)];
			instanciateNodeList(MeshHeadingImpl.class, nodes, array);
			return array;
		} catch (Exception e) {
			new RuntimeException(e);
		}
		return new MeshHeading[0];
	}
	
	@Override
	public Chemical[] chemicals() {
		try {
			L nodes = evaluateAsNodeSet("ChemicalList/Chemical");
			Chemical[] chemicals = new ChemicalImpl[context.size(nodes)];
			instanciateNodeList(ChemicalImpl.class, nodes, chemicals);
			return chemicals;
		} catch (Exception e) {
			new RuntimeException(e);
		}
		return new Chemical[0];
	}

	@Override
	public String pmid() {
		return evaluateAsString("PMID");
	}

	@Override
	public String[] geneSymbols() {
		try {
			L nodes = evaluateAsNodeSet("GeneSymbolList/GeneSymbol");
			String[] geneSymbols = new String[context.size(nodes)];
			for (int i = 0; i < geneSymbols.length; i++) {
				N n = context.get(i, nodes);
				XPathExpression expression = context.getExpression(".");
				Object evaluate = expression.evaluate(n, XPathConstants.STRING);
				geneSymbols[i] = (String) evaluate;
			}
			return geneSymbols;
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	@Override
	public Article article() {
		N base = evaluateAsNode("Article");
		return new ArticleImpl<N, L>(context, base);
	}

	@Override
	public ELocationID eLocationId() {
		N base = evaluateAsNode("ELocationID");
		return base == null ? null : new ELocationIDImpl<N, L>(context, base);
	}
}
