package edu.mssm.icahn.mapreduce.SETH;

import jargs.gnu.CmdLineParser;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Job;
import org.apache.hadoop.mapreduce.lib.input.FileInputFormat;
import org.apache.hadoop.mapreduce.lib.output.FileOutputFormat;
import org.apache.hadoop.mapreduce.lib.output.TextOutputFormat;
import org.apache.hadoop.util.GenericOptionsParser;


import edu.mssm.icahn.mapreduce.common.Configure;
import edu.mssm.icahn.mapreduce.reducer.SimpleTextReducer;
import edu.mssm.icahn.parsers.XmlInputFormat;

public class SETHExtractor {

	private static boolean pmcParser;
	private static boolean pmidParser;
	private static boolean txtParser;

	public static void main(String[] args) throws Exception{
		Configuration conf = new Configuration();	
		args = new GenericOptionsParser(conf, args).getRemainingArgs();
		parseArgs(args);

		//long milliSeconds = 1000*60*60; //Set time out to 60 Minutes. SETH Takes some time for some tasks
		//conf.setLong("mapred.task.timeout", milliSeconds);
		conf.setLong("mapred.task.timeout", 0);

		conf.set(Configure.SETH_MODEL, "resources/mutations.txt");

		if(pmidParser){			
			conf.set(Configure.START_TAG_KEY, "<MedlineCitation ");
			conf.set(Configure.END_TAG_KEY, "</MedlineCitation>");
			conf.set(Configure.XML_PARSER,"medline");
		}
		else if(pmcParser){
			conf.set(Configure.START_TAG_KEY, "<article ");
			conf.set(Configure.END_TAG_KEY, "</article>");
			conf.set(Configure.XML_PARSER,"PMC");
		}
		else if(txtParser){
			conf.set(Configure.START_TAG_KEY, "<PDF2TXT ");
			conf.set(Configure.END_TAG_KEY, "</PDF2TXT>");
			conf.set(Configure.XML_PARSER, "TXT");
		}

		Job job = Job.getInstance(conf);
		job.setJarByClass(SETHExtractor.class);


		job.setInputFormatClass(XmlInputFormat.class); //Format of the input file
		job.setOutputFormatClass(TextOutputFormat.class);	//How do we want to have the output?	

		job.setOutputKeyClass(Text.class);	//Key Output Mapper
		job.setOutputValueClass(Text.class); //Value Output Mapper


		//Set Mapper and Reducer Class
		job.setMapperClass(SETHMapper.class);
		job.setReducerClass(SimpleTextReducer.class);

		//Set In and Output files
		args = new GenericOptionsParser(conf, args).getRemainingArgs();
		FileInputFormat.addInputPath(job, new Path(args[0]));
		FileOutputFormat.setOutputPath(job, new Path(args[1]));

		job.waitForCompletion(true);
	}

	private static void parseArgs(String args[]){
		CmdLineParser parser = new CmdLineParser();
		CmdLineParser.Option pmcParserOption  = parser.addBooleanOption('p', "pmc");
		CmdLineParser.Option pubmedParserOption  = parser.addBooleanOption('m', "medline");
		CmdLineParser.Option txtParserOption  = parser.addBooleanOption('t', "pdf2txt");

		try {
			parser.parse(args);
		}
		catch ( CmdLineParser.OptionException e ) {
			printUsage();
			System.exit(2);
		}

		pmcParser = (Boolean)parser.getOptionValue(pmcParserOption, false);
		pmidParser = (Boolean)parser.getOptionValue(pubmedParserOption, false);
		txtParser = (Boolean)parser.getOptionValue(txtParserOption, false);				

		if(pmcParser && pmidParser || pmcParser && txtParser || pmidParser && txtParser)
			throw new RuntimeException("To many parsers are set");

		if(!pmcParser && !pmidParser && !txtParser )
			throw new RuntimeException("No parser is set");
		System.out.println("ssss");
		System.out.println("Using parser for " +(pmcParser ? "pmc":"") +(pmidParser ? "pmid yy 7777":"") +(txtParser ? "txt2pubmed":""));
	}

	private static void printUsage() {
		System.err.println(
				"Detects mutations in PubMed abstracts and PMC fulltexts" +
						"Usage:\n" +
						//						"Input folder [-i,--in]\n" +
						//						"Output folder [-o, --out]\n" +
				"Parsertype [-p, --parser] --t pdf2txt PMC/Pubmed");
	}

}
