package de.hu.wbi.parser.pubmed.impl;

import de.berlin.hu.wbi.common.xml.NodeBasedClass;
import de.hu.wbi.parser.pubmed.iface.Topic;

public class TopicImpl<N,L> extends NodeBasedClass<N,L> implements Topic {

	@Override
	public String topic() {
		return evaluateAsString(".");
	}

	@Override
	public boolean isMajor() {
		return evaluateAsBoolean("@MajorTopicYN='Y'");
	}

}
