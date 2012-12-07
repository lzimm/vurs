package net.vurs.service.definition.nlp;

import java.io.File;

import opennlp.tools.lang.english.Tokenizer;

import net.vurs.util.ErrorControl;
import net.vurs.util.RelativePath;

public class StringTokenizer {
	public static String ENGLISH_TOKEN_MODEL = "EnglishTok.bin.gz";
	
	private Tokenizer tokenizer = null;
	
	public StringTokenizer() {
		File dataDir = new File(RelativePath.root().getAbsolutePath()
								.concat(File.separator)
								.concat("nlp")
								.concat(File.separator)
								.concat("opennlp")
								.concat(File.separator)
								.concat("models")
								.concat(File.separator)
								.concat("english"));

		try {
			this.tokenizer = new Tokenizer(dataDir.getAbsolutePath()
													.concat(File.separator)
													.concat("tokenize")
													.concat(File.separator)
													.concat(ENGLISH_TOKEN_MODEL));
		} catch (Exception e) {
			ErrorControl.logException(e);
		}
	}
	
	public String[] tokenize(String body) {
		return this.tokenizer.tokenize(body);
	}
	
}
