package edu.mssm.icahn.parsers.tika;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.Reader;
import java.io.StringReader;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import edu.mssm.icahn.parsers.Citation;

/**
 * Represents a text article parsed by apache tikka. The format looks as follows:
 * <PDF2TXT PMC="PMC-PMID-originalfilename">
 * Document content for this file between these two tags; 
 * </PDF2TXT>
 * 
 * @author philippe
 *
 */
public class TxtIndexer  implements Citation {

	private String id;			//Each file has a unique ID (Filename)
	private Integer pmid;			//Most files have a PubMed-ID
	private int pmc;			//Each file has a PMC field; implemented for later usage	
	private String fileType;	//We are just interested to know what kind of file we analyzed
	private String text;		//All inside of the  "PDF2TXT" tags is raw content	

	static Pattern pmcPattern  = Pattern.compile("PMC=\"(.*)\\.txt\">");
	private BufferedReader reader;

	/**
	 * Simple example using the TxtIndexer to read a small file 
	 * @param args
	 * @throws Exception
	 */
	static public void main(String[] args) throws Exception {

		String meFile = "C://Users/lih16/Desktop/pmid.txt";		
		BufferedReader br = new BufferedReader(new FileReader(new File(meFile)));
		StringBuilder sb = new StringBuilder();
		while(br.ready()){
			String line = br.readLine();
			if(line.startsWith("<PDF2TXT"))
				sb = new StringBuilder();
			sb.append(line);
			sb.append("\n");
			
			if(line.startsWith("</PDF2TXT")){				
				TxtIndexer indexer = new TxtIndexer(new StringReader(sb.toString()));
				indexer.parse();
				
				System.out.println(indexer.getId());
				System.out.println(indexer.getPMC());
				System.out.println(indexer.getPmid());		
				System.out.println(indexer.getFileType());
				System.out.println("------------");
			}
		}
		br.close();
		
		
//		String meFile = "200000.txt";
//		File xmlFile = new File(meFile);
//		TxtIndexer indexer = new TxtIndexer(new InputStreamReader(new FileInputStream(xmlFile)));
//		indexer.parse();
//		System.out.println(indexer.getId());
//		System.out.println(indexer.getPMC());
//		System.out.println(indexer.getPmid());
//		System.out.println(indexer.getText());
	}

	/**
	 * Simple constructor; initializing inputstream 
	 * and the internal variables
	 * @param inputStream
	 */
	public TxtIndexer(Reader inputStream) {
		super();
		this.reader = new BufferedReader(inputStream);
		
		this.id = null;
		this.pmid = -1;
		this.pmc = -1 ;
		this.fileType = null;		
		this.text = null;
	}


	/**
	 * Parses the pseudo XML file using a simple methodology..
	 * @throws IOException
	 */
	public void parse() throws IOException{

		StringBuilder sb = new StringBuilder();
		while(reader.ready()){
			String line = reader.readLine();
			
			if(line.startsWith("<PDF2TXT")){
				
				Matcher m  = pmcPattern.matcher(line);
				if(m.find()){
					try{
						this.id = m.group(1);
						System.out.println(this.id);
						this.pmc = Integer.parseInt(this.id.substring(0, this.id.indexOf("-")));
						System.out.println(this.pmc);
						System.out.println(id.indexOf("-")+1);
						System.out.println(id.indexOf("-", id.indexOf("-")+1));
						
						
						//System.exit(0);
						String pmidTmp=id.substring(id.indexOf("-")+1, id.indexOf("-", id.indexOf("-")+1));
						if(!pmidTmp.equals("null"))
							this.pmid = Integer.parseInt(id.substring(id.indexOf("-")+1, id.indexOf("-", id.indexOf("-")+1)));
						this.fileType = this.id.substring(id.lastIndexOf('.')+1).toLowerCase();	
					}catch(Exception ex){
						System.err.println("Problems with '" +this.id +"'");						
						ex.printStackTrace();	
						throw new RuntimeException();
					}
				}
				else{
					System.err.println("Error finding PMC-ID for '" +line +"'");
				}
				
				//There might be some text following the opening tag
				sb.append(line.substring(line.indexOf("\">")+2));
				sb.append("\n");
			}
			
			//After observing the closing tag we stop parsing 
			else if(line.startsWith("</PDF2TXT>")){
				this.text = sb.toString();
				reader.close();
				return;
			}
			else {
				sb.append(line);
				sb.append("\n");
			}				
		}		
		reader.close(); //Should be unreachable as we end parsing after the closing tag..			
	}

	@Override
	public String getId() {
		return id;
	}

	@Override
	public int getPMC() {
		return pmc;
	}

	@Override
	public int getPmid() {
		return pmid;
	}
		
	@Override
	public String getText() {
		return text;
	}

	public String getFileType() {
		return fileType;
	}


	/**
	 * All following methods defined in the Interface Citation are not implemented
	 * The reason simply is that title, abstract, ... are usually not known for raw textfiles..
	 */

	/**
	 * Not implemented
	 */
	@Override
	public String getTitle() {
		return null;
	}


	/**
	 * Not implemented
	 */
	@Override
	public String getAbstr() {
		return null;
	}

	/**
	 * Not implemented
	 */
	@Override
	public String getYear() {
		return null;
	}

	/**
	 * Not implemented
	 */
	@Override
	public String getFirstAuthor() {
		return null;
	}

	/**
	 * Not implemented
	 */
	@Override
	public String getJournal() {
		return null;
	}
}
