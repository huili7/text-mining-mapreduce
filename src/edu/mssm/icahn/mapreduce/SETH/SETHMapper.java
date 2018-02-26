package edu.mssm.icahn.mapreduce.SETH;

import java.io.BufferedWriter;
import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.StringWriter;
import java.util.List;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.LongWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Mapper;

import au.com.bytecode.opencsv.CSVWriter;

import seth.SETH;
import de.hu.berlin.wbi.objects.MutationMention;
import de.hu.berlin.wbi.objects.MutationMention.Tool;
import edu.mssm.icahn.mapreduce.common.CitationGetter;
import edu.mssm.icahn.mapreduce.common.Configure;
import edu.mssm.icahn.mapreduce.common.FileRenamer;
import edu.mssm.icahn.parsers.Citation;

public class SETHMapper extends Mapper<LongWritable, Text, Text, Text>{	

	private static SETH seth;
	private static String parser;


	static enum SETHCounter {numberOfIterations}//This is just a counter used to tell Hadoop, that SETH is still working

	@Override
	protected void setup(Context context) throws IOException, InterruptedException {
		super.setup(context);
		Configuration conf = context.getConfiguration();


		parser = conf.get("parser");
		File file = FileRenamer.copyFileToTmp(new Path(conf.get(Configure.SETH_MODEL)), conf);		
		
		//Use the exact grammar for txt-files from apache tika. It seems that these files have low quality and t 
		//if(parser.equals("TXT"))
			seth =  new SETH(file.getAbsolutePath(), true, true);
		//else
		//	seth =  new SETH(file.getAbsolutePath(), false, true);
	}

	@Override 
	protected void map (LongWritable key, Text value, Context context) throws IOException, InterruptedException {
		//context.write(new Text("1"), new Text("vv"));
		//context.write(new Text("2"),  new Text("v888888888v"));
		//FileSystem fs = FileSystem.get(context.getConfiguration());
		//Path path = new Path("/hpc/users/lih16/temp.txt");
		//OutputStream os = fs.create(path);
		// write to os
		//os.write(value.getBytes());
		//os.close();
		//context.write(new Text("2"),  value);
		//System.exit(0);
		
		/*Path pt=new Path("HAHAER");
        FileSystem fs = FileSystem.get(new Configuration());
        BufferedWriter br=new BufferedWriter(new OutputStreamWriter(fs.create(pt,true)));
                                   // TO append data to a file, use fs.append(Path f)
        String line;
        line="Disha Dishu Daasha";
        System.out.println(line);
        br.write("aaaa"+value.toString());
        br.close();
        System.exit(0);*/
		Citation indexer = CitationGetter.getIndexer(parser, value);
		context.getCounter(SETHCounter.numberOfIterations).increment(1l);
		context.progress();

		StringWriter sw = new StringWriter();
		CSVWriter csv = new CSVWriter(sw);
		//context.write(new Text("1"), new Text(indexer.getText().trim()));
		//context.write(new Text("2"), new Text(indexer.getText().trim()));
		List<MutationMention> mutations = seth.findMutations(indexer.getText());	
		//System.out.println("abstract"+indexer.getText());
		//System.out.println("abstract"+mutations.size());
		
		for (MutationMention mutation : mutations) {    
			try{
				String tmp [] = {
						indexer.getId(),
						indexer.getPmid() <= 0 ? "\\N" : Integer.toString(indexer.getPmid()), //If pmid is unknown
						indexer.getPMC() <= 0 ? "\\N" : Integer.toString(indexer.getPMC()), //If PMC is unknown
						Integer.toString(mutation.getStart()), 
						Integer.toString(mutation.getEnd()),
						mutation.getWtResidue() == null ? "\\N" :  mutation.getWtResidue(),
						mutation.getMutResidue() == null ? "\\N" : mutation.getMutResidue(),
						mutation.getPosition() == null ? "\\N" :   mutation.getPosition(),
//						mutation.getType().toString(),
						mutation.getRef() == null ? "\\N" : mutation.getRef(),	
						mutation.getText().toString(),
						mutation.getTool().toString(),
						mutation.getType().toString(),
						mutation.getTool().equals(Tool.DBSNP) ?  Integer.toString(mutation.getNormalized().get(0).getRsID()) : "\\N", 					
						mutation.getPatternId() == 0 ? "\\N" : Integer.toString(mutation.getPatternId()),
						mutation.toHGVS(),
						"\\N",	// Genes tried to normalize to
						"\\N",	// Matching genes
						"\\N",  // Protein Sequence matching	
						"\\N",	// Protein Sequence matching (entrez-ID)
						"\\N"	// final column..
				};  
				if(tmp != null)
					csv.writeNext(tmp);     		
			}catch(NullPointerException npe){
				System.err.println("Nullpointerexception for " +indexer.getPmid());
				System.err.println("Nullpointerexception for " +mutation.getText());  
				npe.printStackTrace();
				csv.close();
			}

		}        

		csv.close();
		
		if(mutations.size() > 0){
			context.write(new Text(Integer.toString(indexer.getPmid())), new Text(sw.toString().trim()));	
		}
	}
}
