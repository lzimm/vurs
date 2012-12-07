package net.vurs.service.definition.nlp;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.regex.Pattern;

import de.tudarmstadt.ukp.wiktionary.api.PartOfSpeech;
import de.tudarmstadt.ukp.wiktionary.api.Wiktionary;
import de.tudarmstadt.ukp.wiktionary.api.WordEntry;

import net.vurs.service.definition.nlp.regex.RegexStrings;
import net.vurs.service.definition.nlp.token.enumeration.TokenWordType;
import net.vurs.util.ErrorControl;

@SuppressWarnings("serial")
public class WiktionaryDictionary {
	public static String WIKTIONARY_DUMP = "/usr/local/wiktionary/en_wiktionary.xml";
	public static String WIKTIONARY_PARSE = "/usr/local/wiktionary/en_wiktionary.parse";
	public static String WIKTIONARY_LANGUAGE = Locale.ENGLISH.getLanguage();
	
	public static Pattern ENTITY_NOUNS = Pattern.compile(RegexStrings.TwitterSpecific.USERNAMES);
	
	private Wiktionary wiktionary = null;
	
	public WiktionaryDictionary() {
		try {
			File parse = new File(WIKTIONARY_PARSE);
			if (!parse.exists() || parse.list().length == 0) {
				Wiktionary.parseWiktionaryDump(WIKTIONARY_DUMP, WIKTIONARY_PARSE, WIKTIONARY_LANGUAGE, false);
			}
			
			this.wiktionary = new Wiktionary(WIKTIONARY_PARSE);
		} catch (Exception e) {
			ErrorControl.logException(e);
		}
	}
	
	public List<TokenWordType> define(String word) {		
		List<TokenWordType> ret = new ArrayList<TokenWordType>();
		
		for (WordEntry e: this.wiktionary.getWordEntries(word)) {
			ret.add(getType(e.getPartOfSpeech()));
		}
		
		if (ENTITY_NOUNS.matcher(word).matches()) {
			ret.add(TokenWordType.NOUN);
		}
		
		return ret;
	}
	
	private static HashMap<PartOfSpeech, TokenWordType> POS_TO_TYPE = new HashMap<PartOfSpeech, TokenWordType>() {{
		this.put(PartOfSpeech.ADJECTIVE, TokenWordType.ADJECTIVE);
		this.put(PartOfSpeech.ADVERB, TokenWordType.ADVERB);
		this.put(PartOfSpeech.NOUN, TokenWordType.NOUN);
		this.put(PartOfSpeech.VERB, TokenWordType.VERB);
		this.put(PartOfSpeech.ARTICLE, TokenWordType.ARTICLE);
		this.put(PartOfSpeech.CONJUNCTION, TokenWordType.CONJUNCTION);
		this.put(PartOfSpeech.PROPER_NOUN, TokenWordType.NOUN);
	}};
	
	private TokenWordType getType(PartOfSpeech pos) {
		TokenWordType type = POS_TO_TYPE.get(pos);
		if (type == null) type = TokenWordType.UNKNOWN;
		return type;
	}
	
	@Override
	protected void finalize() throws Throwable {
		this.wiktionary.close();
	}
	
}
