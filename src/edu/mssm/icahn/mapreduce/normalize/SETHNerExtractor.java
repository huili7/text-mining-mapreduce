package edu.mssm.icahn.mapreduce.normalize;

import java.io.IOException;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Job;
import org.apache.hadoop.mapreduce.lib.input.FileInputFormat;
import org.apache.hadoop.mapreduce.lib.input.TextInputFormat;
import org.apache.hadoop.mapreduce.lib.output.FileOutputFormat;
import org.apache.hadoop.mapreduce.lib.output.TextOutputFormat;
import org.apache.hadoop.util.GenericOptionsParser;



public class SETHNerExtractor {

	public static void main(String[] args) throws IOException, ClassNotFoundException, InterruptedException {
		Configuration conf = new Configuration();
		conf.set("seth.property", "resources/myProperty.xml");
		Job job = Job.getInstance(conf);

		
		job.setJarByClass(SETHNerExtractor.class);
		
		
		job.setInputFormatClass(TextInputFormat.class); //Simple line inputformat
		job.setOutputFormatClass(TextOutputFormat.class);	//How do we want to have the output?	
		
		job.setOutputKeyClass(Text.class);	//Key Output Mapper
		job.setOutputValueClass(Text.class); //Value Output Mapper
						
	
		//Set Mapper and Reducer Class
		job.setMapperClass(SETHNerMapper.class);
		job.setReducerClass(SETHNerReducer.class);

		//Set In and Output files
		args = new GenericOptionsParser(conf, args).getRemainingArgs();
		FileInputFormat.addInputPath(job, new Path(args[0]));
		FileOutputFormat.setOutputPath(job, new Path(args[1]));
		job.setNumReduceTasks(10);
		
		job.waitForCompletion(true);
		
	}
	

}
