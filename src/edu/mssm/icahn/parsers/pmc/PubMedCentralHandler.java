package edu.mssm.icahn.parsers.pmc;


import de.berlin.hu.wbi.common.layer.Handler;

public interface PubMedCentralHandler extends Handler {

	void handleAbstract(CharSequence abstractText) throws Throwable;

	void handleEpubDay(CharSequence day) throws Throwable;

	void handleIssn(CharSequence issn) throws Throwable;

	void handleIssue(CharSequence issue) throws Throwable;

	void handleJournal(CharSequence journal) throws Throwable;

	void handleEpubMonth(CharSequence month) throws Throwable;

	void handleVolume(CharSequence volume) throws Throwable;

	void handleEpubYear(CharSequence year) throws Throwable;

	void handleAuthor(CharSequence author) throws Throwable;

	void handleSectionTitle(CharSequence sectionTitle) throws Throwable;

	void handleSectionContent(CharSequence sectionContent) throws Throwable;

	void handleArticleTitle(CharSequence articleTitle) throws Throwable;
	
	void handleEndDocument() throws Throwable;

	void handlePubMedCentralId(CharSequence pubMedCentralId) throws Throwable;

	void handlePubMedId(CharSequence pumMedId) throws Throwable;

	void handleCaption(CharSequence caption) throws Throwable;

	void handlePpubDay(CharSequence day) throws Throwable;

	void handlePpubMonth(CharSequence month) throws Throwable;

	void handlePpubYear(CharSequence year) throws Throwable;
}
