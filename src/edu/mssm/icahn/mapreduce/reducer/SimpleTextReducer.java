package edu.mssm.icahn.mapreduce.reducer;

import java.io.IOException;

import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Reducer;

public class SimpleTextReducer extends Reducer<Text, Text, Text, Text> {

	/**
	 * Simple reducer, returining a printed version of results
	 */
	public void reduce (Text key, Iterable<Text> values, Context context) throws IOException, InterruptedException {

		for(Text value : values){
			context.write(new Text(value), null);	
		}	
	}
}
