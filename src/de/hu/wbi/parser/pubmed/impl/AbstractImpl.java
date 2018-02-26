package de.hu.wbi.parser.pubmed.impl;

import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpression;
import javax.xml.xpath.XPathExpressionException;

import de.berlin.hu.wbi.common.xml.Context;
import de.berlin.hu.wbi.common.xml.NodeBasedClass;
import de.hu.wbi.parser.pubmed.iface.Abstract;

public class AbstractImpl<N, L> extends NodeBasedClass<N, L> implements Abstract {

	public AbstractImpl(Context<N, L> context, N base) {
		super(context, base);
	}

	@SuppressWarnings("unchecked")
	@Override
	public String textWithoutTruncationMarker() {
		StringBuilder b = new StringBuilder();
		L set = evaluateAsNodeSet("AbstractText");
		for (int i = 0; i < context.size(set); i++) {
			N n = context.get(i, set);
			
			XPathExpression e = context.getExpression(".");
			try {
				String text = (String) e.evaluate(n, XPathConstants.STRING);
				System.out.println(i+"aaaaaaaaaa"+text);
				
				b.append(text).append(' ');
			} catch (XPathExpressionException e1) {
				e1.printStackTrace();
			}
		}
		return b.toString().trim();
	}

}
