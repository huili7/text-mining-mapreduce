package de.hu.wbi.parser.pubmed;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactoryConfigurationException;

import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import de.berlin.hu.wbi.common.io.IO;
import de.hu.berlin.wbi.objects.MutationMention;
import de.hu.wbi.parser.pubmed.iface.Abstract;
import de.hu.wbi.parser.pubmed.iface.Article;
import de.hu.wbi.parser.pubmed.iface.Author;
import de.hu.wbi.parser.pubmed.iface.AuthorList;
import de.hu.wbi.parser.pubmed.iface.Chemical;
import de.hu.wbi.parser.pubmed.iface.ELocationID;
import de.hu.wbi.parser.pubmed.iface.Journal;
import de.hu.wbi.parser.pubmed.iface.JournalIssue;
import de.hu.wbi.parser.pubmed.iface.MedlineCitation;
import de.hu.wbi.parser.pubmed.iface.MeshHeading;
import de.hu.wbi.parser.pubmed.iface.Name;
import de.hu.wbi.parser.pubmed.iface.PubDate;
import de.hu.wbi.parser.pubmed.iface.PublicationType;
import de.hu.wbi.parser.pubmed.iface.Topic;
import seth.SETH;

public class Example implements MedlineHandler {
	
	int count = 0;
	
	public static void main(String[] args) throws FileNotFoundException, IOException, SAXException, ParserConfigurationException, XPathExpressionException, XPathFactoryConfigurationException {
		String file = args[0];
		InputStream input = IO.open(file);
		InputSource source = new InputSource(input);
		MedlineParser parser = new MedlineParserSaxon();
		Example indexer = new Example();
		parser.setHandler(indexer);
		parser.parse(source);
		
		//seth =  new SETH("", false, true);
	}

	@Override
	public void handle(MedlineCitation citation) {
		System.out.print(citation.pmid() + ": ");
		try {
			
			System.out.println("acccccccccccccc"+citation.article().abstrct().textWithoutTruncationMarker());
			String temp=citation.article().abstrct().textWithoutTruncationMarker();
			SETH seth =  new SETH("/hpc/users/lih16/resources/mutations.txt",true,true);//"C://Users/lih16/Desktop/RunTextMing/textmining/resources/mutations.txt", true, true);
			List<MutationMention> mutations = seth.findMutations(temp);
			for (MutationMention mutation : mutations) { 
			
			 System.out.println(mutation.getText());
			}
			System.exit(0);;
		} catch (Exception e) {
			System.out.println();
		}
//		oldHandle(citation);
	}
	
	public void oldHandle(MedlineCitation citation) {
		System.out.println("Citation: " + count++);
		//System.exit(0);
		Article article = citation.article();
		if (article != null) {
			Abstract abstrct = article.abstrct();
			if (abstrct != null) {
				System.out.println("aaa "+abstrct);
				System.exit(0);
				String textWithoutTruncationMarker = abstrct.textWithoutTruncationMarker();
				print(textWithoutTruncationMarker);
			}
			String affiliation = article.affiliation();
			print(affiliation);
			String articleTitleText = article.articleTitleText();
			print(articleTitleText);
			AuthorList authorList = article.authorList();
			if (authorList != null) {
				for (Author author : authorList) {
					if (author != null) {
						String foreName = author.foreName();
						print(foreName);
						String initials = author.initials();
						print(initials);
						String lastName = author.lastName();
						print(lastName);
						Name name = author.name();
						if (name != null) {
							String fullName = name.fullName();
							print(fullName);
						}
					}
				}
			}
			Journal journal = article.journal();
			if (journal != null) {
				JournalIssue journalIssue = journal.journalIssue();
				if (journalIssue != null) {
					String issue = journalIssue.issue();
					print(issue);
					PubDate pubDate = journalIssue.pubDate();
					if (pubDate != null) {
						String day = pubDate.day();
						print(day);
						String month = pubDate.month();
						print(month);
						String year = pubDate.year();
						print(year);
					}
					String volume = journalIssue.volume();
					print(volume);
				}
				String title = journal.title();
				print(title);
			}
			String pagination = article.pagination();
			print(pagination);
			PublicationType[] publicationTypes = article.publicationTypes();
			for (PublicationType publicationType : publicationTypes) {
				System.out.println("!!!!!!!!!!!!!!!!!!");
				print(publicationType.toString());
			}
		}
		ELocationID eLocationId = citation.eLocationId();
		if (eLocationId != null) {
			boolean valid = eLocationId.isValid();
			print(String.valueOf(valid));
			boolean doi = eLocationId.isPII();
			print(String.valueOf(doi));
			String id = eLocationId.getID();
			print(id);
		}
		Chemical[] chemicals = citation.chemicals();
		if (chemicals != null) {
			for (Chemical chemical : chemicals) {
				String nameOfSubstance = chemical.nameOfSubstance();
				print(nameOfSubstance);
			}
		}
		String[] geneSymbols = citation.geneSymbols();
		if (geneSymbols != null) {
			for (String geneSymbol : geneSymbols) {
				print(geneSymbol);
			}
		}
		MeshHeading[] meshHeadings = citation.meshHeadings();
		for (MeshHeading meshHeading : meshHeadings) {
			Topic[] topics = meshHeading.topics();
			for (Topic topic : topics) {
				boolean major = topic.isMajor();
				print(String.valueOf(major));
				String topic2 = topic.topic();
				print(topic2);
			}
		}
		String pmid = citation.pmid();
		print(pmid);
	}

	public void print(String o) {
		if (o != null && !o.trim().isEmpty()) {
			System.out.println(o);
		}
	}
}
