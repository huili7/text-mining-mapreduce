package edu.mssm.icahn.parsers.tika;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

import org.apache.tika.exception.TikaException;
import org.apache.tika.io.TikaInputStream;
import org.apache.tika.metadata.Metadata;
import org.apache.tika.metadata.TikaMetadataKeys;
import org.apache.tika.parser.AutoDetectParser;
import org.apache.tika.parser.ParseContext;
import org.apache.tika.parser.Parser;
import org.apache.tika.parser.ParserDecorator;
import org.apache.tika.sax.BodyContentHandler;
import org.xml.sax.ContentHandler;
import org.xml.sax.SAXException;

/**
 * RecursiveDocumentParser capable of handling different types of documents using Apache Tika
 * File containers (e.g., tarball and zip) are unpacked and all files are encrypted
 * This strategy is performed until all files and packed subfolders are parsed
 * This code is inspired by http://wiki.apache.org/tika/RecursiveMetadata  
 * @author philippe
 *
 */
public class RecursiveDocumentParser  extends ParserDecorator{

	private static final long serialVersionUID = 1L;
	private Map<Metadata,ContentHandler> contents; //Results of Tika-parsing are stored in a Hashmap, as we potentially parse more than one document


	@Override
	public void parse(InputStream stream, ContentHandler ignore, Metadata metadata, ParseContext context) throws IOException, SAXException, TikaException {

		String fileName = ((TikaInputStream) stream).getFile().toString(); 


		//Ignore specific file formats (e.g., figures, and other stuff mostly consisting of metadata)
		//This step here is mostly used to increase parsing speed
		String file = metadata.get(TikaMetadataKeys.RESOURCE_NAME_KEY);
		if(file != null && (file.endsWith("jpg") || file.endsWith("gif") || file.endsWith("tif") || file.endsWith("tiff") || file.endsWith("nxml") || file.endsWith("wmf") || file.endsWith("aln") || file.endsWith("fasta") || file.endsWith("class") || file.endsWith("nxml") ))
			return;

		//Parse the document...
		ContentHandler content = new BodyContentHandler(Integer.MAX_VALUE);
		super.parse(stream, content, metadata, context); //Parse the document

		if(metadata.get(TikaMetadataKeys.RESOURCE_NAME_KEY) == null){        	
			metadata.set(Metadata.RESOURCE_NAME_KEY, fileName);     
		}

		//Do not add content  of container files (e.g., zip, tarball, etc.) 
		//However, we still need to parse these documents, to recourse into the files.
		if(metadata.get(Metadata.CONTENT_TYPE).equals("application/x-gzip") ||  metadata.get(Metadata.CONTENT_TYPE).equals("application/x-tar") )
			return;

		contents.put(metadata, content);        
	}

	public static void main(String[] args) throws Exception {
		RecursiveDocumentParser parser = new RecursiveDocumentParser(new AutoDetectParser());
		ParseContext context = new ParseContext();
		context.set(Parser.class, parser);



		InputStream stream = TikaInputStream.get(new File("/home/philippe/workspace/mssm/a/00/00/mds526.PMC3574550.pdf"));
		//				InputStream stream = TikaInputStream.get(new File("/home/philippe/workspace/mssm/a/00/00/Ann_Oncol_2013_Mar_12_24(3)_843-850.tar.gz"));
		//		InputStream stream = TikaInputStream.get(new File("/home/philippe/workspace/mssm/a/00/02/Br_J_Cancer_1977_Jan_35(1)_78-86.tar.gz"));


		try {
			parser.parse(stream, new BodyContentHandler(), new Metadata(), context);
		} finally {
			stream.close();
		}

		int i=0;
		for(Metadata key : parser.contents.keySet()){
			System.out.println(key.get(Metadata.CONTENT_TYPE));
			System.out.println(i++ +" " +key.get(TikaMetadataKeys.RESOURCE_NAME_KEY) +" " +parser.contents.get(key).toString().length());
			System.out.println("++++++++++++++");

			//			System.out.println(parser.contents.get(key));
			//			correct(parser.contents.get(key).toString());

		}
	}

	public RecursiveDocumentParser(Parser parser) {
		super(parser);
		contents = new HashMap<Metadata, ContentHandler>();
	}

	public Map<Metadata, ContentHandler> getContents() {
		return contents;
	}
}


