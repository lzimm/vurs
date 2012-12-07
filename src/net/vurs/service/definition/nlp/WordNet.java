package net.vurs.service.definition.nlp;

import java.io.File;
import java.io.FileInputStream;

import net.didion.jwnl.JWNL;
import net.didion.jwnl.data.IndexWord;
import net.didion.jwnl.data.POS;
import net.didion.jwnl.data.Synset;
import net.didion.jwnl.dictionary.Dictionary;
import net.vurs.util.ErrorControl;
import net.vurs.util.RelativePath;

public class WordNet {
	public static String WORDNET_PROPERTIES = "file_properties.xml";
	
	private Dictionary dict = null;
	
	public WordNet() {
		try {			
			JWNL.initialize(new FileInputStream(RelativePath.root().getAbsolutePath()
					.concat(File.separator)
					.concat("resources")
					.concat(File.separator)
					.concat("jwnl")
					.concat(File.separator)
					.concat(WORDNET_PROPERTIES)));
			
			this.dict = Dictionary.getInstance();
		} catch (Exception e) {
			ErrorControl.logException(e);
		}
	}
	
	public Synset get(String word, POS pos) {
		try {
			IndexWord idx = this.dict.getMorphologicalProcessor().lookupBaseForm(pos, word);
			if (idx == null) return null;
			return idx.getSense(1);
		} catch (Exception e) {
			ErrorControl.logException(e);
			return null;
		} 
	}

	public Dictionary getDictionary() {
		return this.dict;
	}
	
}
