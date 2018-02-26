package edu.mssm.icahn.parsers.pmc;

import java.io.File;
import java.io.FileReader;
import java.io.FilenameFilter;
import java.io.IOException;
import java.io.Reader;

import org.xml.sax.SAXException;

import de.berlin.hu.wbi.common.layer.FileHandler;
import de.berlin.hu.wbi.common.layer.TextHandler;
import de.berlin.hu.wbi.common.misc.StringBuilders;

public class PmcFileProcessor  {

	private FileHandler fileHandler;
	private TextHandler textHandler;
	private StringBuilder content = new StringBuilder();
	
	private static final FilenameFilter PMC_FILE_FILTER = new FilenameFilter() {
		@Override
		public boolean accept(File dir, String name) {
			boolean isFile = new File(dir, name).isFile();
			boolean hasCorrectExtension = name.endsWith(".nxml");
			return isFile && hasCorrectExtension;
		}
	};

	public PmcFileProcessor(TextHandler handler, FileHandler fileHander) throws SAXException {
		this.textHandler = handler;
		this.fileHandler = fileHander;
	}
	
	public void handleReader(Reader reader) throws Throwable{
		
		content.setLength(0);		
		StringBuilders.appendReaderContent(content, reader);
		textHandler.handleText(content);
	}


	public void handleFile(File file) throws Throwable {
		if (PMC_FILE_FILTER.accept(file.getParentFile(), file.getName())) {
			if (fileHandler != null) {
				fileHandler.handleFile(file);
			}
			FileReader reader = new FileReader(file);
			content.setLength(0);
			StringBuilders.appendReaderContent(content, reader);
			reader.close();
			String doctypeTag = removeDoctypeTag(content);
			if (doctypeTag.contains("issue-admin")) {
				throw new IOException("Doctype '" + doctypeTag + "'  skipped.");
			} else {
				textHandler.handleText(content);
			}
		} else {
			throw new Exception("File " +file.getCanonicalPath() + " was not accepted.");
		}
	}

	public static String removeDoctypeTag(StringBuilder content) {
		int start = content.indexOf("<!DOCTYPE");
		int end = content.indexOf(">", start) + 1;
		String doctype = content.substring(start, end);
		content.delete(start, end);
		return doctype;
	}

}
