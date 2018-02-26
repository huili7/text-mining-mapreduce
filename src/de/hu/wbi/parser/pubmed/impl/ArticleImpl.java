package de.hu.wbi.parser.pubmed.impl;

import java.util.Collections;
import java.util.Iterator;

import de.berlin.hu.wbi.common.xml.Context;
import de.berlin.hu.wbi.common.xml.NodeBasedClass;
import de.hu.wbi.parser.pubmed.iface.Abstract;
import de.hu.wbi.parser.pubmed.iface.Article;
import de.hu.wbi.parser.pubmed.iface.Author;
import de.hu.wbi.parser.pubmed.iface.AuthorList;
import de.hu.wbi.parser.pubmed.iface.Journal;
import de.hu.wbi.parser.pubmed.iface.PublicationType;

public class ArticleImpl<N,L> extends NodeBasedClass<N,L> implements Article {

	public ArticleImpl(Context<N, L> context, N node) {
		super(context, node);
	}

	@Override
	public Journal journal() {
		N base = evaluateAsNode("Journal");
		return new JournalImpl<N, L>(context, base);
	}

	@Override
	public Abstract abstrct() {
		N base = evaluateAsNode("Abstract");
		if (base == null) {
			return null;
		}
		return new AbstractImpl<N, L>(context, base);
	}

	@Override
	public AuthorList authorList() {
		N base = evaluateAsNode("AuthorList");
		if (base == null) {
			return new AuthorList() {
				@Override
				public Iterator<Author> iterator() {
					return Collections.<Author>emptyList().iterator();
				}
			};
		}
		return new AuthorListImpl<N,L>(context, base);
	}

	@Override
	public String articleTitleText() {
		return evaluateAsString("ArticleTitle");
	}

	@Override
	public String affiliation() {
		return evaluateAsString("Affiliation");
	}

	@Override
	public String pagination() {
		return evaluateAsString("Pagination");
	}

	@Override
	public PublicationType[] publicationTypes() {
		L set = evaluateAsNodeSet("PublicationTypeList/PublicationType");
		PublicationType[] publicationTypes = new PublicationType[context.size(set)];
		instanciateNodeList(PublicationTypeImpl.class, set, publicationTypes);
		return publicationTypes ;
	}
}
