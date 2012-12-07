package net.vurs.service.definition.nlp.token.permutation;

import java.util.ArrayList;
import java.util.List;

import net.vurs.service.definition.nlp.token.TokenInterpolation;
import net.vurs.service.definition.nlp.token.enumeration.TokenAlignment;
import net.vurs.service.definition.nlp.token.enumeration.TokenWordType;

public class TokenInterpolationPermutation extends TokenPermutation<TokenInterpolation> {

	public TokenInterpolationPermutation() {
		super();
	}
	
	@Override
	public TokenInterpolationPermutation construct(String value, String key, List<TokenWordType> types) {
		TokenInterpolationPermutation ret = new TokenInterpolationPermutation();
		ret.permutations.add(new TokenInterpolation(value));
		ret.addDimension(value, key, 0, TokenAlignment.START, types);
		return ret;
	}
	
	@Override
	public TokenInterpolationPermutation copy() {
		TokenInterpolationPermutation ret = new TokenInterpolationPermutation();
		ret.permutations.addAll(this.permutations);
		return ret;
	}
	
	@Override
	protected TokenInterpolation copy(TokenInterpolation permutation) {
		return permutation.copy();
	}

	@Override
	protected TokenInterpolation add(TokenInterpolation permutation, String key,
			int distance, TokenAlignment alignment, TokenWordType type) {
		permutation.addModifier(key, distance, alignment, type);
		return permutation;
	}

	@Override
	public List<TokenInterpolation> reduce(String prefix) {
		ArrayList<TokenInterpolation> reductions = new ArrayList<TokenInterpolation>(this.permutations.size());
		
		for (TokenInterpolation t: this.permutations) {
			if (t.getModifiers().isEmpty()) continue;
			
			t.setPrefix(prefix);
			if (t.containsValue()) {
				reductions.add(t);
			}
		}
		
		return reductions;
	}

}
