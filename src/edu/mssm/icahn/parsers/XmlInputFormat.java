package edu.mssm.icahn.parsers;

import org.apache.hadoop.io.LongWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.InputSplit;
import org.apache.hadoop.mapreduce.RecordReader;
import org.apache.hadoop.mapreduce.TaskAttemptContext;
import org.apache.hadoop.mapreduce.lib.input.TextInputFormat;


/**
 * An XML TextInputFormat that handles MedlineCitations.
 * 
 * @author J&ouml;rg Hakenberg &lt;joerg.hakenberg@mssm.edu&gt;
 */
public  class XmlInputFormat extends TextInputFormat {

	public RecordReader<LongWritable, Text> createRecordReader(
			InputSplit split, TaskAttemptContext context) {
		return new XmlRecordReader();
	}	
}