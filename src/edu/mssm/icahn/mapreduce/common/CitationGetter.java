package edu.mssm.icahn.mapreduce.common;

import java.io.IOException;
import java.io.InputStream;
import java.io.StringReader;

import org.apache.hadoop.io.Text;
import org.xml.sax.InputSource;

import de.berlin.hu.wbi.common.io.IO;
import de.berlin.hu.wbi.common.xml.AdvancedXmlHandler;
import de.berlin.hu.wbi.common.xml.XmlReader;
import de.hu.wbi.parser.pubmed.Example;
import de.hu.wbi.parser.pubmed.MedlineParser;
import de.hu.wbi.parser.pubmed.MedlineParserSaxon;
import edu.mssm.icahn.parsers.Citation;
import edu.mssm.icahn.parsers.pmc.PhilDocumentBuilder;
import edu.mssm.icahn.parsers.pmc.PmcFileProcessor;
import edu.mssm.icahn.parsers.pmc.PubMedCentralParser;
import edu.mssm.icahn.parsers.pmc.PubMedCentralParser.Event;
import edu.mssm.icahn.parsers.pubmed.CitationIndexer;
import edu.mssm.icahn.parsers.tika.TxtIndexer;


public class CitationGetter {
	
	//For PMC2Txt we sometimes obtain huge articles.  (up to 800MB); unfortunately Java 
//	has problems converting Hadoop Text objects into strings. We therefore trim long texts
//	into smaller peaces of 100000000 characters. 	
//	TODO: In the future we could think about a different way to handle such files
	private static int maxChar = 100000000;  
	
	public static Citation getIndexer(String parserType, Text value){
		//InputStream input = IO.open(value.toString());
		//InputSource source = new InputSource(input);
		//InputSource inSource = new InputSource( new StringReader( value.toString() ) );
		/*InputSource inSource = new InputSource( new StringReader( value.toString() ) );
		CitationIndexer indexer = new CitationIndexer();
		indexer.setHandler(indexer);
		indexer.parse(inSource);
		System.err.println("Indexer" +indexer.getText() +" unknown!");
		System.exit(1);
		return indexer;*/	
		//InputSource inSource = new InputSource( new StringReader( value.toString() ) );
		//InputSource inSource = new InputSource(  IO.open(value.toString()) );
		//MedlineParser parser = new MedlineParserSaxon();
		//CitationIndexer indexer = new CitationIndexer();
		//indexer.setHandler(indexer);
		//indexer.parse(inSource);
		
		//InputSource source = new InputSource(input);
		//MedlineParser parser = new MedlineParserSaxon();
		
		//parser.setHandler(indexer);
		//parser.parse(source);
		//System.err.println("Indexer" +indexer.getText() +" unknown!");
		//System.exit(1);
		//return indexer;		
		if(parserType.equals("medline")){
			//InputStream input = IO.open(value.toString());
			//InputSource source = new InputSource(input);
			InputSource inSource = new InputSource( new StringReader( value.toString() ) );
			//InputSource inSource = new InputSource(  IO.open(value.toString()) );
			CitationIndexer indexer = new CitationIndexer();
			indexer.setHandler(indexer);
			indexer.parse(inSource);
			//System.err.println("Indexer" +indexer.getText() +" unknown!");
			//System.exit(1);
			return indexer;		
		}
		else if(parserType.equals("PMC")){
			PhilDocumentBuilder indexer = new PhilDocumentBuilder();
			AdvancedXmlHandler<Event> pmcParser = new PubMedCentralParser(indexer);
			try{
				XmlReader xmlReader = new XmlReader(pmcParser);
				xmlReader.getXmlReader().setErrorHandler(null);
				PmcFileProcessor fileHandler = new PmcFileProcessor(xmlReader, indexer);
				fileHandler.handleReader(new StringReader(value.toString()));
			
			}
			catch(Throwable a){
				a.printStackTrace();
				System.exit(1);
			}
			return indexer;			
		}
		else if(parserType.equals("TXT")){
						
			try {
				TxtIndexer indexer  = new TxtIndexer(new StringReader(Text.decode(value.getBytes(), 0, Math.min(maxChar, value.getLength()))+"\n</PDF2TXT>"));
				indexer.parse();				
				return indexer;
			} catch (IOException e) {
				e.printStackTrace();
				System.exit(1);
			} 			
		}
		
		System.err.println("Indexer" +value +" unknown!");
		System.exit(1);
		return null;
	
		
	}

}
