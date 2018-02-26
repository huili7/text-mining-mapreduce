package edu.mssm.icahn.parsers.pubmed;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.xml.sax.InputSource;

import de.hu.wbi.parser.pubmed.MedlineHandler;
import de.hu.wbi.parser.pubmed.MedlineParser;
import de.hu.wbi.parser.pubmed.MedlineParserSaxon;
import de.hu.wbi.parser.pubmed.iface.Abstract;
import de.hu.wbi.parser.pubmed.iface.Article;
import de.hu.wbi.parser.pubmed.iface.Author;
import de.hu.wbi.parser.pubmed.iface.AuthorList;
import de.hu.wbi.parser.pubmed.iface.JournalIssue;
import de.hu.wbi.parser.pubmed.iface.MedlineCitation;
import de.hu.wbi.parser.pubmed.iface.PubDate;
import edu.mssm.icahn.parsers.Citation;

public class CitationIndexer implements MedlineHandler, Citation {	
	private static MedlineParser PARSER = new MedlineParserSaxon();

	private int pmid; 
	private String title;
	private String abstr;
	private String year;
	private List<String> authors;
	private String journal;

	public CitationIndexer() {
		super();
		authors = new ArrayList<String>();
	}

	public void setHandler(MedlineHandler indexer){		
		PARSER.setHandler(indexer);			
	}

	public void parse(InputSource inSource){
		PARSER.parse(inSource);	
	}

	@Override
	public void handle(MedlineCitation citation) {

		this.pmid = Integer.parseInt(citation.pmid());	

		Article article = citation.article();
		this.title = article.articleTitleText();//Title

		Abstract abstr = article.abstrct();
		if (abstr != null)				
			this.abstr = abstr.textWithoutTruncationMarker();//Abstract						
		else 
			this.abstr = "";

		//PubYear
		JournalIssue issue = article.journal().journalIssue();
		PubDate date = issue.pubDate();
		if (date.isStructured()) {
			this.year = date.year();
		}
		else{
			String plainStringDate = date.toPlainString();
			this.year = extractYear(plainStringDate);	
		}

		AuthorList authorList = article.authorList();
		if (authorList != null){
			for(Iterator<Author> authorIterator = authorList.iterator(); authorIterator.hasNext();){
				Author author = authorIterator.next();
				this.authors.add(author.name().fullName());
			}
		}

		this.journal = article.journal().title();
	}

	private  static String extractYear(String dateString) {
		Matcher m = YEAR_PATTERN.matcher(dateString);
		m.find();
		return m.group();
	}
	private  static final Pattern YEAR_PATTERN = Pattern.compile("((19|20)\\d\\d)");

	
	
	@Override
	public String getId() {
		return Integer.toString(getPmid());
	}

	@Override
	public int getPmid() {
		return pmid;
	}
	
	@Override
	public int getPMC() {
		return -1;
	}

	@Override
	public String getTitle() {
		return title;
	}

	@Override
	public String getAbstr() {
		return abstr;
	}

	@Override
	public String getText(){
		StringBuilder sb = new StringBuilder();
		sb.append(title);
		sb.append("\n");
		sb.append(abstr);
		return sb.toString();
	}

	@Override
	public String getYear() {
		return year;
	}

	@Override
	public String getFirstAuthor() {
		if(authors.size() > 0 )
			return "";
		
		return authors.get(0).toString();
	}
	
	@Override
	public String getJournal() {
		return journal;
	}
}