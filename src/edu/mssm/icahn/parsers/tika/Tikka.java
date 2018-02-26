package edu.mssm.icahn.parsers.tika;

import jargs.gnu.CmdLineParser;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.zip.GZIPInputStream;

import org.apache.tika.exception.TikaException;
import org.apache.tika.io.TikaInputStream;
import org.apache.tika.metadata.Metadata;
import org.apache.tika.metadata.TikaMetadataKeys;
import org.apache.tika.parser.AutoDetectParser;
import org.apache.tika.parser.ParseContext;
import org.apache.tika.parser.Parser;
import org.apache.tika.sax.BodyContentHandler;
import org.xml.sax.SAXException;

import au.com.bytecode.opencsv.CSVReader;

public class Tikka {

	private RecursiveDocumentParser parser;
	private static String outDirectory;
	private static String inDirectory;
	private static String mappingDirectory;
	Map<String, Integer> fileToPMC; //Filename to PMC-ID map
	Map<Integer, Integer> pmc2pmid; //Filename to PMC-ID map
	private int resolution=10000;

	public static void main(String[] args) throws IOException, SAXException, TikaException {

		Tikka TikaParser = new Tikka();
		//parseArgs(args);
		mappingDirectory="C://Users/lih16/Desktop/PMC_ids/";
		outDirectory="C://Users/lih16/Desktop/PMC_ids/output";
		inDirectory="C://Users/lih16/Desktop/PMC_ids/input";
		TikaParser.parsePmc2Pmid(mappingDirectory +"PMC-ids.csv.gz");
		TikaParser.parseFile2Pmc(mappingDirectory +"file_list.pdf.csv");
		//TikaParser.parsePmc2Pmid(mappingDirectory +"PMC-ids.csv.gz");
		//TikaParser.parseFile2Pmc(mappingDirectory +"file_list.pdf.csv");
		//TikaParser.parseFile2Pmc(mappingDirectory +"file_list.csv");		

		File inDir = new File(inDirectory);
		TikaParser.parse(inDir);

		System.out.println(inDirectory +" completed");
	}

	/**
	 * Parses a file or directory using the recursive parser, which by itself cycles file containers
	 * @param file
	 * @throws FileNotFoundException
	 * @throws IOException
	 * @throws SAXException
	 * @throws TikaException
	 */
	private void parse(File file) throws FileNotFoundException, IOException, SAXException, TikaException{

		if(file.isDirectory()){
			for(File f: file.listFiles()){
				parse(f);
			}				
		}

		else if(file.isFile() && !file.isHidden()){

			Integer pmcid = fileToPMC.get(file.getName());
			if(pmcid == null){
				System.out.println("Skipping file '" +file.getName() +"' .. No PMC-ID!");
				return;
			}

			parser = null;
			parser = new RecursiveDocumentParser(new AutoDetectParser());
			ParseContext context = new ParseContext();
			context.set(Parser.class, parser);

			InputStream stream = null;

			try {
				stream = TikaInputStream.get(file);
				parser.parse(stream, new BodyContentHandler(), new Metadata(), context);
			}catch(Exception ex){
				System.err.println("Skipping file '" +file.getAbsolutePath() +"' due to '" +ex.getMessage() +"'");
				return;
			}
			finally {
				if(stream != null)
					stream.close();
			}

			int floorPMCID = (int) (Math.floor(pmcid/resolution)*resolution);
			File parent = new File(outDirectory +Integer.toString(floorPMCID));
			if(!parent.exists())
				parent.mkdirs();

			forloop:for(Metadata key : parser.getContents().keySet()){

				//On top we skip document types such as figures, but sometimes we still parse documents containing only metadata. 
				//Here we remove these contents
				if(parser.getContents().get(key).toString().length() <= 5)
					continue forloop;

				String filename = key.get(TikaMetadataKeys.RESOURCE_NAME_KEY);
				
				String pattern = Pattern.quote(System.getProperty("file.separator"));
				
				System.out.println(System.getProperty("file.separator")+"!"+filename);
				//System.exit(0);
				
				filename = filename.substring(filename.lastIndexOf(System.getProperty("file.separator"))+1);

				//Ignore licence files
				if(filename.equals("license.txt"))
					continue forloop;

				//Often a PMC archive contains the article as PDF twice.
				//Once as PDF containing a PMC1234 pattern
				//Once in the tar archive without this pattern..
				//Therefore we remove the PMC to keep it consistent and potentially overwrite the file..
				//				filename = filename.replaceAll("PMC[1-9][0-p]*", ""); 

				File f;
				if(pmc2pmid.containsKey(pmcid))
					 f = new File(parent, Integer.toString(pmcid) +"-" +Integer.toString(pmc2pmid.get(pmcid)) +"-" +filename +".txt");
				else
					f = new File(parent, Integer.toString(pmcid) +"-" +"null" +"-" +filename +".txt");
				try{
					BufferedWriter bw = new BufferedWriter(new FileWriter(f));
					bw.write(correct(parser.getContents().get(key).toString()));
					bw.close();	
				}
				catch(Exception ex){
					System.err.println("Problem writing parsed results for " +file.getAbsolutePath() +" into " +f.getAbsolutePath());
					ex.printStackTrace();
				}				
			}		
		}
	}

	/**
	 *Stores the filename to PMC mapping using the CSV-Reader
	 * @param file
	 * @throws IOException
	 */
	private void parseFile2Pmc(String file) throws IOException{

		CSVReader reader = new CSVReader(new FileReader(file));

		String [] elements = reader.readNext();
		while ((elements = reader.readNext()) != null) {
			if(!elements[2].startsWith("PMC"))
				throw new RuntimeException("This should not happen!");
			
			fileToPMC.put(elements[0].substring(elements[0].lastIndexOf("/")+1), Integer.parseInt(elements[2].substring(3)));	
		}
		reader.close();				
	}

	private void parsePmc2Pmid(String file) throws IOException{
		FileInputStream fis = new FileInputStream(file);
		GZIPInputStream gis = new GZIPInputStream(fis);
		InputStreamReader isr = new InputStreamReader(gis);
		BufferedReader br = new BufferedReader(isr);
		CSVReader reader = new CSVReader(br, ',');
		

		String [] elements = reader.readNext();
		while ((elements = reader.readNext()) != null) {
				if(!elements[8].startsWith("PMC"))
					throw new RuntimeException("This should not happen!");
								
				Integer pmc = Integer.parseInt(elements[8].substring(3));
								
				try{
					Integer pmid = Integer.parseInt(elements[9]);
					pmc2pmid.put(pmc, pmid);
				}catch(NumberFormatException nfe){} //In case the pubmed ID remains unknown
		}
		reader.close();				
	}

	/**
	 * This method is used to refine parsed text
	 * Especially it merges two lines into one line if both lines contain content
	 * If the previous line ends with an hyphen the hyphen is removed if it matches the properNoun pattern
	 * @param text
	 */
	private static String correct(String text){
		Matcher matcher;
		StringBuilder sb = new StringBuilder();
		Pattern properNoun = Pattern.compile("([0-9])|([A-Z]{2,})");

		String previousline ="";
		Scanner scanner = new Scanner(text);
		while(scanner.hasNextLine()){ //Iterate linewise over text
			String line = scanner.nextLine();

			//Previous line and this line contain at least one character..
			if(line.matches("^\\s*$") == false && previousline.matches("^\\s*$") == false){
				sb.deleteCharAt(sb.length()-1); //Remove line-break

				//Special handling if we observe a hyphen
				if(previousline.endsWith("-")){
					String token1 = previousline;
					String token2 = line;

					try{
						token1 = previousline.substring(previousline.lastIndexOf(' '));
						token2 = line.substring(0, line.indexOf(' '));
					}catch(StringIndexOutOfBoundsException e){}

					matcher = properNoun.matcher(token1+token2);
					if(!matcher.find()){
						sb.deleteCharAt(sb.length()-1); //Delete hyphenation
						//						System.out.println("Hyphenation '" +token1+token2 +"'"); //Just for our amusement
					}
					else{
						//						System.out.println("No Hyphenation '" +token1+token2 +"'"); //Just for our amusement
					}
				}

				//If we observe not a hyphen at the end we need a whitespace to seperate the two words...
				else{
					sb.append(" ");
				}
			}

			sb.append(line);
			sb.append("\n");
			previousline = line;
		}
		scanner.close();		
		return sb.toString();
	}


	public Tikka() {
		super();
		fileToPMC = new HashMap<String, Integer>();
		pmc2pmid = new HashMap<Integer, Integer>();
	}

	private static void parseArgs(String args[]){
		CmdLineParser parser = new CmdLineParser();
		CmdLineParser.Option inFolderOption  = parser.addStringOption('i', "in");
		CmdLineParser.Option outFolderOption  = parser.addStringOption('o', "out");
		CmdLineParser.Option mappingFolderOption  = parser.addStringOption('m', "mapping");

		try {
			parser.parse(args);
		}
		catch ( CmdLineParser.OptionException e ) {
			printUsage();
			System.exit(2);
		}


		String s= (String)   parser.getOptionValue(inFolderOption,  "/home/philippe/workspace/mssm/a/00/00/");
		if(s != null)
			inDirectory =s;

		s= (String)  parser.getOptionValue(outFolderOption, "/sc/orga/projects/PBG/KBase/work/PubMedCentralTXT/");
		if(s != null)
			outDirectory= s;

		s= (String)  parser.getOptionValue(mappingFolderOption, "/home/philippe/workspace/mssm/mappings/");
		if(s != null)
			mappingDirectory= s;

		//        System.out.println("Starting parser for " +inDirectory);
		//        System.out.println("Saving result to " +outDirectory);
		//        System.out.println("Using mapping " +mappingDirectory);

	}

	private static void printUsage() {
		System.err.println(
				"Parses all different kind of texts (pdf, doc, etc.)" +
						"Usage:\n" +
						"Input folder [-i,--in]\n" +
						"Output folder [-o, --out]\n" +
				"Mapping folder [-m, --mapping]");
	}
}
