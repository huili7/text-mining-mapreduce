package edu.mssm.icahn.mapreduce.identifier;

import java.io.IOException;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.io.LongWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Mapper;

import edu.mssm.icahn.mapreduce.common.CitationGetter;
import edu.mssm.icahn.parsers.Citation;
import edu.mssm.icahn.parsers.tika.TxtIndexer;

public class IdMapper  extends Mapper<LongWritable, Text, Text, Text>  { 

	private static String parser;

	@Override
	protected void setup(Context context) throws IOException, InterruptedException {
		super.setup(context);
		Configuration conf = context.getConfiguration();


		parser = conf.get("parser");
	}


	@Override 
	protected void map (LongWritable key, Text value, Context context) throws IOException, InterruptedException {

		Citation indexer = CitationGetter.getIndexer(parser, value);
			
		if(indexer instanceof TxtIndexer){
			TxtIndexer a = (TxtIndexer) indexer;
			context.write(new Text(indexer.getId()), new Text(indexer.getId() +"\t" +a.getFileType()));	
		}
		else
			context.write(new Text(indexer.getId()), new Text(indexer.getId()));
					
	}
}
