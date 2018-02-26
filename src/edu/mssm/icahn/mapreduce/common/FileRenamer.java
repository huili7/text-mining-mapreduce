package edu.mssm.icahn.mapreduce.common;

import java.io.File;
import java.io.IOException;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;

public class FileRenamer {

	/**
	 * Copies a file from the HDFS to the local directory of each executing node
	 * @param inputFile
	 * @param conf
	 * @return
	 * @throws IOException
	 */
	public static File copyFileToTmp(Path inputFile, Configuration conf) throws IOException {
		FileSystem fs = FileSystem.get(inputFile.toUri(), conf);
		String file = inputFile.getName();
		String suffix = file.substring(file.lastIndexOf("."));
		File outputFile = File.createTempFile(inputFile.getName(), suffix);
		outputFile.deleteOnExit();
		fs.copyToLocalFile(inputFile, new Path(outputFile.getAbsolutePath()));
		
		return outputFile;
	}

}
