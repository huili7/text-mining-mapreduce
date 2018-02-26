package edu.mssm.icahn.mapreduce.normalize;

import java.io.IOException;

import org.apache.hadoop.io.LongWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Mapper;

public class SETHNerMapper extends Mapper<LongWritable, Text, Text, Text>{	

	@Override 
	protected void map (LongWritable key, Text value, Context context) throws IOException, InterruptedException {
		String pmid = value.toString().split("\t")[0];
		
		context.write(new Text(pmid), value);
		
	}
}
