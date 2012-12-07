package net.vurs.service.definition.nlp;

import java.io.File;

import opennlp.tools.dictionary.Dictionary;
import opennlp.tools.lang.english.PosTagger;
import opennlp.tools.postag.POSDictionary;
import opennlp.tools.postag.TagDictionary;

import net.vurs.util.ErrorControl;
import net.vurs.util.RelativePath;

public class POSTagger {
	public static String ENGLISH_TAG_DICT = "tagdict.txt";
	public static String ENGLISH_TAG_MODEL = "tag.bin.gz";
	
	private Dictionary dict = null;
	private TagDictionary tagDict = null;
	private PosTagger tagger = null;
	
	public POSTagger() {
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
			this.dict = new Dictionary();
			
			this.tagDict = new POSDictionary(dataDir.getAbsolutePath()
													.concat(File.separator)
													.concat("postag")
													.concat(File.separator)
													.concat(ENGLISH_TAG_DICT));
			
			this.tagger = new PosTagger(dataDir.getAbsolutePath()
													.concat(File.separator)
													.concat("postag")
													.concat(File.separator)
													.concat(ENGLISH_TAG_MODEL), 
										this.dict,
										this.tagDict);
		} catch (Exception e) {
			ErrorControl.logException(e);
		}
	}
	
	public String tag(String sentence) {
		return this.tagger.tag(sentence);
	}
	
	public String[] tag(String[] tokens) {
		return this.tagger.tag(tokens);
	}
	
}
