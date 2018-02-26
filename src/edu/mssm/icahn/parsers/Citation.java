package edu.mssm.icahn.parsers;

/**
 * Interface representing a citation (e.g., PubMed, PMC, ...)
 * @author philippe
 *
 */
public interface Citation {
	
	public String getId();
	
	public int getPmid();
	
	public int getPMC();

	public String getTitle();

	public String getAbstr();
	
	public String getText();

	public String getYear();

	public String getFirstAuthor();

	public String getJournal();
	
	

}
