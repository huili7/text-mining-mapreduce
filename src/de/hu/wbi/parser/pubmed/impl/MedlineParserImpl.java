package de.hu.wbi.parser.pubmed.impl;

import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpressionException;

import org.xml.sax.InputSource;

import de.berlin.hu.wbi.common.xml.NodeBasedClass;
import de.hu.wbi.parser.pubmed.MedlineHandler;
import de.hu.wbi.parser.pubmed.MedlineParser;

public abstract class MedlineParserImpl<N, L> extends NodeBasedClass<N, L> implements MedlineParser {

	private MedlineHandler indexer;

	@Override
	public void setHandler(MedlineHandler indexer) {
		this.indexer = indexer;
	}
	
	@Override
	public void parse(InputSource inSource) {
		try {
			N base = getBase(inSource);
			setNodeBase(base);
			L list = evaluateAsNodeSet("//MedlineCitation");
			for (int i = 0; i < context.size(list); i++) {
				
				N citationNode = context.get(i, list);
				MedlineCitationImpl<N,L> citation = new MedlineCitationImpl<N, L>();
				citation.inherit(context, citationNode);
				indexer.handle(citation);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@SuppressWarnings("unchecked")
	public N getBase(InputSource inSource) {
		try {
			N base = (N) context.getXPath().evaluate("/", inSource, XPathConstants.NODE);
			return base;
		} catch (XPathExpressionException e) {
			e.printStackTrace();
		}
		return null;
	}

}
