package edu.mssm.icahn.parsers.pmc;


import java.io.IOException;
import java.util.EnumSet;

import org.xml.sax.Attributes;

import de.berlin.hu.wbi.common.xml.AdvancedXmlHandler;

public class PubMedCentralParser extends AdvancedXmlHandler<PubMedCentralParser.Event> {
	private PubMedCentralHandler handler;
	private final StringBuilder captionBuilder = new StringBuilder();
	private final StringBuilder buffer = new StringBuilder();
	private final StringBuilder bodyBuilder = new StringBuilder();
	
	public PubMedCentralParser(PubMedCentralHandler handler) {
		super(EnumSet.noneOf(Event.class));
		this.handler = handler;
		
		//Keep in mind that the eventMap keys are iterated in insertion order!
		
		//at first absolute paths:
		eventMap.put("/article/front/article-meta", Event.Meta);
		eventMap.put("/article/front/article-meta/abstract", Event.Abstract);
		eventMap.put("/article/front/article-meta/abstract/title", Event.SubSectionTitle);
		eventMap.put("/article/front/article-meta/article-id", Event.Id);
		eventMap.put("/article/front/article-meta/contrib-group/contrib", Event.AuthorContrib);
		eventMap.put("/article/front/article-meta/contrib-group/contrib/name", Event.Name);
		eventMap.put("/article/front/article-meta/contrib-group/contrib/name/given-names", Event.GivenNames);
		eventMap.put("/article/front/article-meta/contrib-group/contrib/name/suffix", Event.NameSuffix);
		eventMap.put("/article/front/article-meta/pub-date/day", Event.Day);
		eventMap.put("/article/front/article-meta/pub-date/month", Event.Month);
		eventMap.put("/article/front/article-meta/pub-date", Event.PubDate);
		eventMap.put("/article/front/article-meta/pub-date/year", Event.Year);
		eventMap.put("/article/front/article-meta/title-group/article-title", Event.ArticleTitle);
		eventMap.put("/article/front/article-meta/volume", Event.Volume);
		eventMap.put("/article/front/journal-meta/journal-title", Event.JournalTitle);
		eventMap.put("/article/front/journal-meta/journal-title-group/journal-title", Event.JournalTitle);
		eventMap.put("/article/front/article-meta/issue", Event.Issue);
		eventMap.put("/article/front/journal-meta/issn", Event.Issn);
		eventMap.put("/article/body", Event.Body);
		eventMap.put("/article/body/p", Event.BodyParagraph);
		eventMap.put("/article/body/sec/title", Event.SectionTitle);
		eventMap.put("/article/body/sec", Event.Section);
		eventMap.put("/article/body/sec/sec", Event.SecSec);
		
		//some relative paths:
		eventMap.put("/list/list-item/p", Event.ListTextItem);
		eventMap.put("/preformat", Event.Preformat);
		eventMap.put("/sec/title", Event.SubSectionTitle);
		eventMap.put("/inline-formula", Event.Formula);
		eventMap.put("/disp-formula", Event.Formula);
		eventMap.put("/table-wrap", Event.TableWrap);
		eventMap.put("/p", Event.Paragraph);
		eventMap.put("/fig", Event.Figure);
		eventMap.put("/caption", Event.Caption);
		eventMap.put("/media", Event.Media);
		eventMap.put("/supplementary-material/label", Event.Label);
		eventMap.put("/title", Event.Title);
	}
	
	public static enum Event {
		_,
		Abstract,
		ArticleTitle,
		AuthorContrib,
		Body, 
		BodyParagraph,
		Caption, 
		Day,
		ElectronicPubDate, 
		Figure, 
		Formula, 
		GivenNames, 
		Id, 
		Issn, 
		Issue, 
		JournalTitle, 
		Label, 
		ListTextItem, 
		Media, 
		Meta, 
		Month,
		Name, 
		PumMedCentralId, 
		PumMedId, 
		Paragraph, 
		Preformat, 
		PrintPubDate, 
		PubDate,
		SecSec,
		Section,
		SectionTitle,
		Title, 
		NameSuffix, 
		SubSectionTitle, 
		TableWrap,
		Volume, 
		Year, 
		}

	@Override
	public Event getEvent() throws Throwable {
		Event event = getEventByPath(Event._);
		
		//Special cases:
		switch (event) {
		case Id:
			if (attributeEquals("pub-id-type", "pmid")) {
				event = Event.PumMedId;
			} else if (attributeEquals("pub-id-type", "pmc")) {
				event = Event.PumMedCentralId;
			}
			break;
		case Section:
			Attributes last = attributesDeque.getLast();
			String value = last.getValue("sec-type");
			if (value != null) {
				value = value.trim();
				if (!value.isEmpty()) {
					handler.handleSectionTitle(value); //Sometimes only source for section title
				}
			}
			break;
		case PubDate:
			if (!attributeEquals("pub-type", "epub")) {
				event = Event.ElectronicPubDate;
			}
			if (!attributeEquals("pub-type", "ppub")) {
				event = Event.PrintPubDate;
			}
			break;
		case AuthorContrib:
			if (!attributeEquals("contrib-type", "author")) {
				event = Event._;
			}
			break;
		case Issn:
			if (!attributeEquals("pub-type", "ppub")) {
				event = Event._;
			}
			break;
		}
		return event;
	}
	
	public void startEvent() throws Throwable {
		Event event = eventDeque.getLast();
		switch (event) {
		case Day:
		case Month:
		case Year:
			if (isActive(Event.PrintPubDate) || isActive(Event.ElectronicPubDate)) {
				clearBuffer();
				enable();
			}
			break;
		case Name:
			if (isActive(Event.AuthorContrib)) {
				clearBuffer();
				enable();
			}
			break;
		case Issn:
		case Abstract:
		case ArticleTitle:
		case JournalTitle:
		case Issue:
		case Volume:
		case PumMedCentralId:
		case PumMedId:
		case SectionTitle:
		case ListTextItem:
		case Body:
			enable();
			break;
		case Section:
			if (buffer.length() > 0) {
				handler.handleSectionContent(buffer);
				clearBuffer();
			}
			enable();
			break;
		case Caption:
			if (isActive(Event.Body) && !isActive(Event.Media)) {
				enable();
			} else {
				disable();
			}
			break;
		case TableWrap:
		case Preformat:
		case Formula:
		case Figure:
		case Label:
			disable();
			break;
		}

		switch (event) {
		case Paragraph:
		case SubSectionTitle:
		case Caption:
			int length = buffer.length();
			if (length > 0 && isEnabled()) {
				buffer.append(' ');
			}
			break;
		case GivenNames:
		case NameSuffix:
			if (buffer.length() > 0) {
				buffer.append(", ");
			}
			break;
		}
	}

	@Override
	protected void endEvent() throws Throwable {
		Event event = eventDeque.getLast();
		switch (event) {
		case Name:
			if (isActive(Event.AuthorContrib)) {
				handler.handleAuthor(buffer);
				clearBuffer();
			}
			break;
		case ArticleTitle:
			handler.handleArticleTitle(buffer);
			clearBuffer();
			break;
		case SectionTitle:
			handler.handleSectionTitle(buffer);
			clearBuffer();
			break;
		case BodyParagraph:
			bodyBuilder.append(' ');
			break;
		case ListTextItem:
			buffer.append(" ");
			break;
		case Section:
			handler.handleSectionContent(buffer);
			clearBuffer();
			break;
		case Meta:
			handler.handleAbstract(buffer);
			clearBuffer();
			break;
		case SubSectionTitle:
			if (isEnabled()) {
				buffer.append(". ");
			}
			break;
		case JournalTitle:
			handler.handleJournal(buffer);
			clearBuffer();
			break;
		case Issue:
			handler.handleIssue(buffer);
			clearBuffer();
			break;
		case Issn:
			handler.handleIssn(buffer);
			clearBuffer();
			break;
		case Volume:
			handler.handleVolume(buffer);
			clearBuffer();
			break;
		case PumMedCentralId:
			handler.handlePubMedCentralId(buffer);
			clearBuffer();
			break;
		case PumMedId:
			handler.handlePubMedId(buffer);
			clearBuffer();
			break;
			
		case Paragraph:
			if (isEnabled() && isActive(Event.Caption)) {
				captionBuilder.append(' ');
			}
			break;
		case Day:
		case Month:
		case Year:
			if (isActive(Event.ElectronicPubDate)) {
				switch (event) {
				case Day:
					handler.handleEpubDay(buffer); 
					break;
				case Month:
					handler.handleEpubMonth(buffer); 
					break;
				case Year: 
					handler.handleEpubYear(buffer); 
					break;
				}
				clearBuffer();
			} else if (isActive(Event.PrintPubDate)) {
				switch (event) {
				case Day:
					handler.handlePpubDay(buffer); 
					break;
				case Month:
					handler.handlePpubMonth(buffer); 
					break;
				case Year: 
					handler.handlePpubYear(buffer); 
					break;
				}
				clearBuffer();
			}
			break;
		case Caption:
			if (isEnabled()) {
				handler.handleCaption(captionBuilder);
				captionBuilder.setLength(0);
			}
			break;
		case Title:
			if (isEnabled() && isActive(Event.Caption)) {
				captionBuilder.append(' ');
			}
			break;
		case Body:
			if (isEnabled() && bodyBuilder.length() > 0) {
				handler.handleSectionContent(bodyBuilder);
				bodyBuilder.setLength(0);
			}
		}
	}

	public void clearBuffer() {
		buffer.setLength(0);
	}
	
	@Override
	public void reset() {
		super.reset();
		clearBuffer();
	}
	
	@Override
	public void handleCharacters(char[] ch, int start, int length) throws IOException {
		if (isEnabled()) {
			if (isActive(Event.Caption)) {
				captionBuilder.append(ch, start, length);
			} else if (isActive(Event.Body) && !isActive(Event.Section)) {
				bodyBuilder.append(ch, start, length);
			} else {
				buffer.append(ch, start, length);
			}
		}
	}
	
	@Override
	public void handleEndDocument() throws Throwable {
		handler.handleEndDocument();
		super.reset();
	}
}