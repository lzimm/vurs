package net.vurs.service.definition.nlp.wrappers;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import net.didion.jwnl.data.POS;
import net.vurs.common.Pair;

@SuppressWarnings("serial")
public class PhraseChunk {
	/*
	 * 	TAGS:
		CC Coord Conjuncn           and,but,or
		CD Cardinal number          one,two
		DT Determiner               the,some
		EX Existential there        there
		FW Foreign Word             mon dieu
		IN Preposition              of,in,by
	 *	JJ Adjective                big
	 *	JJR Adj., comparative       bigger
	 *	JJS Adj., superlative       biggest
		LS List item marker         1,One
		MD Modal                    can,should
	 *	NN Noun, sing. or mass      dog
	 *	NNP Proper noun, sing.      Edinburgh
	 *	NNPS Proper noun, plural    Smiths
	 *	NNS Noun, plural            dogs
		POS Possessive ending       Ís
		PDT Predeterminer           all, both
		PP$ Possessive pronoun      my,oneÍs
		PRP Personal pronoun         I,you,she
	 * 	RB Adverb                   quickly
	 *	RBR Adverb, comparative     faster
	 *	RBS Adverb, superlative     fastest
		RP Particle                 up,off
		SYM Symbol                  +,%,&
		TO ñtoî                     to
		UH Interjection             oh, oops
	 *	VB verb, base form          eat
	 *	VBD verb, past tense        ate
	 *	VBG verb, gerund            eating
	 *	VBN verb, past part         eaten
	 *	VBP Verb, present           eat
	 *	VBZ Verb, present           eats
		WDT Wh-determiner           which,that
		WP Wh pronoun               who,what
		WP$ Possessive-Wh           whose
		WRB Wh-adverb               how,where
		, Comma                     ,
		. Sent-final punct          . ! ?
		: Mid-sent punct.           : ; „
		$ Dollar sign               $
		# Pound sign                #
		" quote                     "
		( Left paren                (
		) Right paren               )
	 */
	
	public static Integer PHRASE_TYPE_OFFSET = 2;
	
	public static enum PhraseType {
		NP, ADVP, VP, PP, O, IN
	};
	
	private static Map<String, PhraseType> phraseMapper = new HashMap<String, PhraseType>() {{
		this.put("NP", PhraseType.NP);
		this.put("ADVP", PhraseType.ADVP);
		this.put("VP", PhraseType.VP);
		this.put("PP", PhraseType.PP);
		this.put("O", PhraseType.O);
		this.put("IN", PhraseType.IN);
	}};
	
	private static Map<PhraseType, Map<String, Integer>> primaries = new HashMap<PhraseType, Map<String, Integer>>() {{
		this.put(PhraseType.NP, new HashMap<String, Integer>() {{
			this.put("NN", 1);
			this.put("NNP", 1);
			this.put("NNPS", 1);
			this.put("NNS", 1);
			this.put("JJ", 0);
			this.put("JJR", 0);
			this.put("JJS", 0);
		}});
	
		this.put(PhraseType.ADVP, new HashMap<String, Integer>() {{
			this.put("RB", 1);
			this.put("RBR", 1);
			this.put("RBS", 1);			
		}});
		
		this.put(PhraseType.VP, new HashMap<String, Integer>() {{
			this.put("VB", 1);
			this.put("VBD", 1);
			this.put("VBG", 2);
			this.put("VBN", 1);
			this.put("VBP", 1);
			this.put("VBZ", 1);
		}});
		
		this.put(PhraseType.PP, new HashMap<String, Integer>() {{
			
		}});
		
		this.put(PhraseType.IN, new HashMap<String, Integer>() {{
			
		}});
	}};
	
	private static Map<PhraseType, POS> posMapper = new HashMap<PhraseType, POS>() {{
		this.put(PhraseType.NP, POS.NOUN);
		this.put(PhraseType.ADVP, POS.ADVERB);
		this.put(PhraseType.VP, POS.VERB);
	}};
	
	private PhraseType type = null;
	private List<String> tokens = null;
	private List<String> tags = null;
	private List<String> chunks = null;
	
	public PhraseChunk(String token, String tag, String chunk) {
		this.tokens = new LinkedList<String>();
		this.tags = new LinkedList<String>();
		this.chunks = new LinkedList<String>();
		
		this.add(token, tag, chunk);
		this.type = PhraseChunk.phraseMapper.get(chunk.substring(PHRASE_TYPE_OFFSET));
	}
	
	public void add(String token, String tag, String chunk) {
		this.tokens.add(token);
		this.tags.add(tag);
		this.chunks.add(chunk);
	}
	
	public String getSubject() {
		if (this.tokens.size() == 1) {
			return this.tokens.get(0);
		}
		
		Pair<Integer, Integer> idx = new Pair<Integer, Integer>(0, Integer.MIN_VALUE);
		for (int i = 0; i < tokens.size(); i++) {
			Integer p = PhraseChunk.primaries.get(this.type).get(tags.get(i));
			if (p == null) p = Integer.MIN_VALUE;
			if (idx.b() < p) idx = new Pair<Integer, Integer>(i, p);
		}
		
		return tokens.get(idx.a());
	}
	
	public String getModifier() {
		if (this.tokens.size() == 1) {
			return null;
		}
		
		Pair<Integer, Integer> idx = new Pair<Integer, Integer>(0, Integer.MIN_VALUE);
		for (int i = 0; i < tokens.size(); i++) {
			Integer p = PhraseChunk.primaries.get(this.type).get(tags.get(i));
			if (p == null) p = Integer.MIN_VALUE;
			if (idx.b() > p) idx = new Pair<Integer, Integer>(i, p);
		}
		
		return tokens.get(idx.a());
	}
	
	public PhraseType getType() {
		return this.type;
	}
	
	public POS getPOS() {
		return PhraseChunk.posMapper.get(this.type);
	}
	
	@Override
	public String toString() {
		return String.format("%s: %s", this.getPOS().getLabel(), this.tokens);
	}

	public List<String> getChunks() {
		return this.chunks;
	}
	
	public List<String> getTags() {
		return this.tags;
	}
	
	public List<String> getTokens() {
		return this.tokens;
	}
}
