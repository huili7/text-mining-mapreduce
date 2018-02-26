package de.hu.wbi.parser.pubmed;

import java.util.HashMap;
import java.util.Map;

import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathExpression;
import javax.xml.xpath.XPathFactory;

import de.berlin.hu.wbi.common.xml.Context;

public abstract class DefaultContext<N, L> implements Context<N, L> {

	private Map<String, XPathExpression> expressionMap;
	private XPath xpath;
	
	public DefaultContext(XPath xpath, Map<String, XPathExpression> map) {
		this.xpath = xpath;
		this.expressionMap = map;
	}
	
	public DefaultContext() {
		this(XPathFactory.newInstance().newXPath(), new HashMap<String, XPathExpression>());
	}
	
	@Override
	public XPathExpression getExpression(String expression) {
		try {
			XPathExpression xPathExpression = expressionMap.get(expression);
			if (xPathExpression == null) {
				xPathExpression = xpath.compile(expression);
				expressionMap.put(expression, xPathExpression);
			}
			return xPathExpression;
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	@Override
	public XPath getXPath() {
		return xpath;
	}

}
