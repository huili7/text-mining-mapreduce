package edu.mssm.icahn.mapreduce.sentence;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.StringWriter;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

import opennlp.tools.lang.english.SentenceDetector;
import opennlp.tools.sentdetect.SentenceDetectorME;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.LongWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Mapper;

import de.hu.berlin.wbi.objects.EntityOffset;

import au.com.bytecode.opencsv.CSVWriter;

import edu.mssm.icahn.mapreduce.common.CitationGetter;
import edu.mssm.icahn.mapreduce.common.Configure;
import edu.mssm.icahn.mapreduce.common.FileRenamer;
import edu.mssm.icahn.parsers.Citation;

public class SentenceMapper  extends Mapper<LongWritable, Text, Text, Text>  { 

	private static SentenceDetectorME sdetector = null; //Sentencedetector
	private static String parser;

	private HashMap<String, Set<EntityOffset>> entities; //Contains a set of entities we would like to get the sentences boundaries 

	@Override
	protected void setup(Context context) throws IOException, InterruptedException {
		super.setup(context);
		Configuration conf = context.getConfiguration();

		File file = FileRenamer.copyFileToTmp(new Path(conf.get(Configure.SENTENCE_MODEL)), conf);		
		sdetector = new SentenceDetector(file.getAbsolutePath());
		parser = conf.get(Configure.XML_PARSER);

		
		entities = new HashMap<String, Set<EntityOffset>>();
		Path pt=new Path(conf.get(Configure.entityFile));		
		FileSystem fs = FileSystem.get(new Configuration());
//		BufferedReader br=new BufferedReader(new InputStreamReader(new GZIPInputStream(fs.open(pt))));			
		BufferedReader br=new BufferedReader(new InputStreamReader(fs.open(pt)));
		while (br.ready()){
			String elements []=br.readLine().split("\t");
			if(elements.length != 3)
				throw new RuntimeException("Number of splits in inputfile is " +elements.length +" instead of 3");

			if(!entities.containsKey(elements[0]))
				entities.put(elements[0], new HashSet<EntityOffset>());

			entities.get(elements[0]).add(new EntityOffset(Integer.parseInt(elements[1]), Integer.parseInt(elements[2])));
		}
		System.out.println("Extracting sentences for " +entities.size());

	}


	@Override 
	protected void map (LongWritable key, Text value, Context context) throws IOException, InterruptedException {

		try{
			Citation indexer = CitationGetter.getIndexer(parser, value);
			StringWriter sw = new StringWriter();
			CSVWriter csv = new CSVWriter(sw);
			//Extract sentence boundaries only for documents in our set of relevant articles
			if(entities.containsKey(indexer.getId())){
				int array [] = sdetector.sentPosDetect(indexer.getText());	//Detect Sentences

				for(EntityOffset offset : entities.get(indexer.getId())){
					int start = 0;
					loop:for(int end : array){
						if(offset.getStart() >= start && offset.getStop() <= end){
							String tmp [] = {
									indexer.getId(),	
									Integer.toString(offset.getStart()),
									Integer.toString(offset.getStop()),
									indexer.getText().substring(offset.getStart(), offset.getStop()),
									Integer.toString(start),
									Integer.toString(end),							
									indexer.getText().substring(start, end)
							};
							csv.writeNext(tmp); 
							break loop;
						}										
						start = end;
					}
				}
				context.write(new Text(Integer.toString(indexer.getPmid())), new Text(sw.toString().trim()));

			}
		}catch(Error ex){
			System.err.println("Cought exceotion. Continuing..");
			ex.printStackTrace();
		}
		


		//		int array [] = sdetector.sentPosDetect(indexer.getText());	//Detect Sentences
		//		int start = 0;
		//		StringWriter sw = new StringWriter();
		//		CSVWriter csv = new CSVWriter(sw);
		//		for(int i : array){
		//			String tmp [] = {
		//					indexer.getId(),
		//					indexer.getPmid() <= 0 ? "\\N" : Integer.toString(indexer.getPmid()), //If pmid is unknown
		//							indexer.getPMC() <= 0 ? "\\N" : Integer.toString(indexer.getPMC()), //If PMC is unknown
		//
		//									Integer.toString(start),
		//									Integer.toString(i)	            
		//									//					indexer.getTitle().substring(start, i); //The actual sentence
		//			};
		//			csv.writeNext(tmp); 
		//		 start = i; 
		//		}	
		//		csv.close();
		//
		//		context.write(new Text(Integer.toString(indexer.getPmid())), new Text(sw.toString().trim()));
	}
}
