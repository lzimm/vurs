package net.vurs.service.definition.nlp;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import opennlp.tools.lang.english.TreebankChunker;

import net.vurs.service.definition.nlp.wrappers.PhraseChunk;
import net.vurs.util.ErrorControl;
import net.vurs.util.RelativePath;

public class PhraseChunker {
	public static String ENGLISH_PHRASE_MODEL = "EnglishChunk.bin.gz";
	
	private TreebankChunker chunker = null;
	
	public PhraseChunker() {
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
			this.chunker = new TreebankChunker(dataDir.getAbsolutePath()
													.concat(File.separator)
													.concat("chunker")
													.concat(File.separator)
													.concat(ENGLISH_PHRASE_MODEL));
		} catch (Exception e) {
			ErrorControl.logException(e);
		}
	}
	
	public String[] chunk(String[] tokens, String[] tags) {
		return this.chunker.chunk(tokens, tags);
	}
	
	public List<PhraseChunk> phraseChunk(String[] tokens, String[] tags, String[] chunks) {
		List<PhraseChunk> ret = new ArrayList<PhraseChunk>();
		
		PhraseChunk curr = null;
		for (int i = 0; i < tokens.length; i++) {
			String token = tokens[i];
			String tag = tags[i];
			String chunk = chunks[i];
			
			System.out.println(String.format("%s (%s / %s)", token, tag, chunk));
			
			if (chunk.charAt(0) == 'B') {
				curr = new PhraseChunk(token, tag, chunk);
				ret.add(curr);
			} else if (chunk.charAt(0) == 'I') {
				curr.add(token, tag, chunk);
			}
		}
		
		return ret;
	}
	
}
