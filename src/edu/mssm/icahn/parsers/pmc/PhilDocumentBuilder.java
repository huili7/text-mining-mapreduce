package edu.mssm.icahn.parsers.pmc;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import de.berlin.hu.wbi.common.layer.FileHandler;
import edu.mssm.icahn.parsers.Citation;


public class PhilDocumentBuilder implements PubMedCentralHandler, FileHandler, Citation {

	private StringBuilder fulltext = new StringBuilder();
	private String sectionTitle;
	private String issn;
	private String abstr;
	private String issue;
	private String journal;
	private String volume;
	private String title;
	private String day;
	private String month;
	private String year;
	private List<String> authors;
	private File source;
	private Matcher matcherPumMedCentralId;
	private int pmid = -1;
	private int pmc = -1;

	final static private Matcher matcherWhiteSpaces = Pattern.compile("\\s+").matcher("");
	final static private Matcher foolStops = Pattern.compile("\\.+").matcher("");

	
	public PhilDocumentBuilder()  {
		this.authors = new ArrayList<String>();
		this.matcherPumMedCentralId = Pattern.compile(".*-(\\d+)").matcher("");
	}
	
	
	private String clean(CharSequence string) {
		String cleaned = matcherWhiteSpaces.reset(string).replaceAll(" ");
		cleaned = foolStops.reset(cleaned).replaceAll(".");
		cleaned.trim();
		return cleaned;
	}
	
	//Handles the title of an article
	@Override
	public void handleArticleTitle(CharSequence articleTitle) throws Throwable {
		String cleanedTitle = clean(articleTitle);
		this.title = cleanedTitle;
		append(fulltext, Fields.title, "\t\t", cleanedTitle, '\n');
	}
	
	//Handles the abstract of an article
	@Override
	public void handleAbstract(CharSequence abstractText) throws Throwable {
		String cleaned = clean(abstractText);
		if (!cleaned.isEmpty()) {
			this.abstr = cleaned; 
			append(fulltext, Fields.abstr, "\t\t", cleaned, '\n');
		}
	}
	
	//Handles the figure and table captions  of an article
	@Override
	public void handleCaption(CharSequence caption) throws Throwable {
		String content = clean(caption);
		if (!content.isEmpty()) {
			append(fulltext, Fields.caption, "\t\t", content, '\n');
		}
	}
	
	//Handles the individual (Intro, Methods, etc.) sections of an article
	@Override
	public void handleSectionContent(CharSequence sectionContent) throws Throwable {
		if (sectionTitle == null || sectionTitle.trim().isEmpty()) {
			sectionTitle = Fields.other;
		}
		String content = clean(sectionContent);
		if (!content.isEmpty()) {
			append(fulltext, sectionTitle, "\t\t", content, '\n');
		}
		sectionTitle = null;
	}



	@Override
	public void handleEpubDay(CharSequence day) throws Throwable {
		this.day = clean(day);
	}

	@Override
	public void handleIssn(CharSequence issn) throws Throwable {
		this.issn = clean(issn);
	}

	@Override
	public void handleIssue(CharSequence issue) throws Throwable {
		this.issue = clean(issue);
	}

	@Override
	public void handleJournal(CharSequence journal) throws Throwable {
		this.journal = clean(journal);
	}

	@Override
	public void handleEpubMonth(CharSequence month) throws Throwable {
		this.month = clean(month);
	}

	@Override
	public void handleVolume(CharSequence volume) throws Throwable {
		this.volume = clean(volume);
	}

	@Override
	public void handleEpubYear(CharSequence year) throws Throwable {
		this.year = clean(year);
	}

	@Override
	public void handleAuthor(CharSequence author) throws Throwable {
		this.authors.add(clean(author));
	}

	@Override
	public void handleSectionTitle(CharSequence sectionTitle) throws Throwable {
		if (this.sectionTitle == null) {
			this.sectionTitle = clean(sectionTitle).toUpperCase();
		}
	}



	@Override
	public void handleEndDocument() throws Throwable{
		
		if (pmc == -1) {
			pmc = getPmcIdFromFilename(source);
		}

		if (pmc == -1) {
			throw new Exception("No PMC-Id found.");
		}
		//System.err.println("full TEXT---"+fulltext);
		removeLastNewLine(fulltext);
		this.day = null;
		this.month = null;
		this.year = null;
	}

	
	private void removeLastNewLine(StringBuilder text) {
		//int last = text.length() - 1;
		//if (text.charAt(last) == '\n') {
		//	text.setLength(last);
		//}
	}

	@Override
	public void handlePubMedCentralId(CharSequence pubMedCentralId) throws Throwable {		
//		append(fulltext, "PMC", "\t\t", pubMedCentralId.toString().trim(), '\n');
		this.pmc = Integer.parseInt(pubMedCentralId.toString().trim());
	}

	@Override
	public void handlePubMedId(CharSequence pumMedId) throws Throwable {
		this.pmid = Integer.parseInt(pumMedId.toString().trim());
	}
	
	private int getPmcIdFromFilename(File file) throws IOException  {
		String path = file.getCanonicalPath();
		if (matcherPumMedCentralId.reset(path).find()) {
			String id = matcherPumMedCentralId.group(1);
			return Integer.parseInt(id);
		}
		return -1;
	}
	
	private void append(Appendable a, Object... o) throws IOException {
		for (Object object : o) {
			a.append(object.toString());
		}
	}

	@Override
	public void handleFile(File file) throws Throwable {
		this.source = file;
		reset();
	}

	public void reset() {
		this.fulltext.setLength(0);
		this.pmid = -1;
		this.pmc = -1;
		this.sectionTitle = null;
	}



	@Override
	public void handlePpubDay(CharSequence day) throws Throwable {
		if (this.day == null) {
			this.day = clean(day); 
		}
	}

	@Override
	public void handlePpubMonth(CharSequence month) throws Throwable {
		if (this.month == null) {
			this.month = clean(month);
		}
	}

	@Override
	public void handlePpubYear(CharSequence year) throws Throwable {
		if (this.year == null) {
			this.year = clean(year);
		}
	}


	@Override
	public int getPmid() {		
		return this.pmid;
	}
	
	@Override
	public String getId() {
		return Integer.toString(getPMC());
	}


	public int getPMC(){
		return this.pmc;
	}


	@Override
	public String getTitle() {
		return this.title;
	}


	@Override
	public String getAbstr() {
		return this.abstr;
	}

	@Override
	public String getYear() {
		return this.year;
	}

	@Override
	public String getFirstAuthor() {
		if(authors.size() > 0 )
			return "";
		
		return authors.get(0).toString();
	}

	@Override
	public String getJournal() {
		return this.journal;
	}
	
	@Override
	public String getText() {
		return fulltext.toString();
	}
}	