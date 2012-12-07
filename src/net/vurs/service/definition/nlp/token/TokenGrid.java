package net.vurs.service.definition.nlp.token;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import net.vurs.common.ConstructingHashMap;
import net.vurs.common.constructor.LinkedListConstructor;
import net.vurs.service.definition.nlp.TweetModel;
import net.vurs.service.definition.nlp.token.enumeration.TokenAlignment;
import net.vurs.service.definition.nlp.token.enumeration.TokenWordType;
import net.vurs.service.definition.nlp.token.permutation.TokenInterpolationPermutation;
import net.vurs.service.definition.nlp.token.permutation.TokenPhrasePermutation;

public class TokenGrid {
	private static final int PHRASE_RADIUS = 4;
	private static final int INTERPOLATION_RADIUS = 4;
	
	protected final Logger logger = LoggerFactory.getLogger(getClass());
	
	private TweetModel tweetModel = null;
	
	private HashSet<String> verbs = null;
	private HashMap<String, List<TokenWordType>> grid = null;
	private ArrayList<String> ordered = null;
	
	public TokenGrid(TweetModel tweetModel) {
		this.tweetModel = tweetModel;
		this.verbs = new HashSet<String>();
		this.ordered = new ArrayList<String>();
		this.grid = new ConstructingHashMap<String, List<TokenWordType>>(new LinkedListConstructor<String, TokenWordType>());
	}
	
	public void add(String token, List<TokenWordType> list) {
		this.ordered.add(token);
		
		HashSet<TokenWordType> seen = new HashSet<TokenWordType>();
		for (TokenWordType type: list) {
			if (seen.add(type)) {
				this.grid.get(token).add(type);
				if (list.equals(TokenWordType.VERB)) {
					this.verbs.add(token);
				}
			}
		}
	}
	
	public List<TokenPhrase> getVerbPhrases() {
		List<TokenPhrase> ret = new ArrayList<TokenPhrase>();
		
		for (String verb: this.verbs) {
			if (this.tweetModel.stop(verb)) continue;
			
			for (String neighbour: this.grid.keySet()) {
				if (neighbour.equals(verb)) continue;
				
				for (TokenWordType wordType: this.grid.get(neighbour)) {
					TokenPhrase phrase = new TokenPhrase(verb);
					phrase.addModifier(neighbour, 0, TokenAlignment.UNKNOWN, wordType);
					ret.add(phrase);
				}
			}
		}
		
		return ret;
	}

	public List<TokenInterpolation> getInterpolationCandidates() {
		List<TokenInterpolation> ret = new ArrayList<TokenInterpolation>();
		int orderedSize = this.ordered.size();
		
		for (int i = 0; i < orderedSize; i++) {
			boolean isCandidate = true;
			for (TokenWordType type: this.grid.get(this.ordered.get(i))) {
				if (type.equals(TokenWordType.ARTICLE) || type.equals(TokenWordType.CONJUNCTION)) {
					isCandidate = false;
					break;
				}
			}
			
			if (isCandidate) {
				ret.addAll(this.getInterpolations(this.ordered.get(i)));
			}
		}
		
		return ret;
	}

	public List<TokenPhrase> getPhrases() {
		return new TokenPhrasePermutation().permute(null, 1, this.grid.size(), PHRASE_RADIUS, this.grid, this.ordered, this.tweetModel);
	}
	
	public List<TokenInterpolation> getInterpolations(String value) {
		return new TokenInterpolationPermutation().permute(value, 2, this.grid.size(), INTERPOLATION_RADIUS, this.grid, this.ordered, this.tweetModel);
	}

	public int size() {
		return this.grid.size();
	}
	
}
