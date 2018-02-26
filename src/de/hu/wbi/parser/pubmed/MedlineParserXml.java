package de.hu.wbi.parser.pubmed;

import javax.xml.xpath.XPathFactoryConfigurationException;

import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import de.hu.wbi.parser.pubmed.impl.MedlineParserImpl;


public class MedlineParserXml extends MedlineParserImpl<Node, NodeList> {

	public MedlineParserXml() throws XPathFactoryConfigurationException {
		this.context = new DefaultContext<Node, NodeList>() {

			@Override
			public Node get(int i, NodeList list) {
				return list.item(i);
			}

			@Override
			public int size(NodeList list) {
				return list.getLength();
			}

			@Override
			public String getName(Node node) {
				return node.getLocalName();
			}

			@Override
			public Node getParent(Node node) {
				return node.getParentNode();
			}
		};
	}
}
