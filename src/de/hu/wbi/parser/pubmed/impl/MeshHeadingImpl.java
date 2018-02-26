package de.hu.wbi.parser.pubmed.impl;

import de.berlin.hu.wbi.common.xml.NodeBasedClass;
import de.hu.wbi.parser.pubmed.iface.MeshHeading;
import de.hu.wbi.parser.pubmed.iface.Topic;

public class MeshHeadingImpl<N,L> extends NodeBasedClass<N,L> implements MeshHeading {
	
	@Override
	public Topic[] topics() {
		L list = evaluateAsNodeSet("./DescriptorName");
		Topic[] topics = new TopicImpl[context.size(list)];
		instanciateNodeList(TopicImpl.class, list, topics);
		return topics;
	}
}
