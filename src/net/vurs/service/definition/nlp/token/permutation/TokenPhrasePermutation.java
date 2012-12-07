package net.vurs.service.definition.nlp.token.permutation;

import java.util.ArrayList;
import java.util.List;

import net.vurs.service.definition.nlp.token.TokenPhrase;
import net.vurs.service.definition.nlp.token.enumeration.TokenAlignment;
import net.vurs.service.definition.nlp.token.enumeration.TokenWordType;

public class TokenPhrasePermutation extends TokenPermutation<TokenPhrase> {

	public TokenPhrasePermutation() {
		super();
	}
	
	@Override
	public TokenPhrasePermutation construct(String value, String key, List<TokenWordType> types) {
		TokenPhrasePermutation ret = new TokenPhrasePermutation();
		ret.permutations.add(new TokenPhrase());
		ret.addDimension(value, key, 0, TokenAlignment.START, types);
		return ret;
	}
	
	@Override
	public TokenPhrasePermutation copy() {
		TokenPhrasePermutation ret = new TokenPhrasePermutation();
		ret.permutations.addAll(this.permutations);
		return ret;
	}
	
	@Override
	protected TokenPhrase copy(TokenPhrase permutation) {
		return permutation.copy();
	}

	@Override
	protected TokenPhrase add(TokenPhrase permutation, String key,
			int distance, TokenAlignment alignment, TokenWordType type) {
		permutation.addModifier(key, distance, alignment, type);
		return permutation;
	}
	
	@Override
	public List<TokenPhrase> reduce(String prefix) {
		ArrayList<TokenPhrase> reductions = new ArrayList<TokenPhrase>(this.permutations.size());
		
		for (TokenPhrase t: this.permutations) {
			if (t.getModifiers().isEmpty()) continue;
			
			t.setPrefix(prefix);
			reductions.add(t);
		}
		
		return reductions;
	}

}
