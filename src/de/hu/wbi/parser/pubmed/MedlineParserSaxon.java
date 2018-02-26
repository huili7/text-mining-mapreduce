package de.hu.wbi.parser.pubmed;

import java.util.List;

import net.sf.saxon.om.NodeInfo;
import de.hu.wbi.parser.pubmed.impl.MedlineParserImpl;

public class MedlineParserSaxon extends
		MedlineParserImpl<NodeInfo, List<NodeInfo>> {

	public MedlineParserSaxon() {
		this.context = new DefaultContext<NodeInfo, List<NodeInfo>>() {
			@Override
			public NodeInfo get(int i, List<NodeInfo> list) {
				return list.get(i);
			}

			@Override
			public int size(List<NodeInfo> list) {
				return list.size();
			}

			@Override
			public String getName(NodeInfo node) {
				return node.getDisplayName();
			}

			@Override
			public NodeInfo getParent(NodeInfo node) {
				return node.getParent();
			}
		};
	}
}
