package de.hu.wbi.parser.pubmed.iface;

import java.util.Iterator;

public interface AuthorList extends Iterable<Author> {
	Iterator<Author> iterator();
}
