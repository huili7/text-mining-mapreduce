package de.hu.wbi.parser.pubmed.impl;

import java.util.Arrays;
import java.util.Iterator;

import de.berlin.hu.wbi.common.xml.Context;
import de.berlin.hu.wbi.common.xml.NodeBasedClass;
import de.hu.wbi.parser.pubmed.iface.Author;
import de.hu.wbi.parser.pubmed.iface.AuthorList;

public class AuthorListImpl<N, L> extends NodeBasedClass<N, L> implements
		AuthorList {
	public AuthorListImpl(Context<N, L> context, N base) {
		super(context, base);
	}

	@Override
	public Iterator<Author> iterator() {
		L authors = evaluateAsNodeSet("Author");
		int size = context.size(authors);
		Author[] authorArray = new Author[size];
		instanciateNodeList(AuthorImpl.class, authors, authorArray);
		return Arrays.asList(authorArray).iterator();
	}

}
