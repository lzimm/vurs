package net.vurs.service.definition.nlp.token.permutation;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;

import net.vurs.service.definition.nlp.TweetModel;
import net.vurs.service.definition.nlp.token.enumeration.TokenAlignment;
import net.vurs.service.definition.nlp.token.enumeration.TokenWordType;

public abstract class TokenPermutation<T> {

	protected List<T> permutations = null;
	
	public TokenPermutation() {
		this.permutations = new ArrayList<T>();
	}
	
	public List<T> permute(String value, int offset, int size, int radius, Map<String, List<TokenWordType>> grid, List<String> ordered, TweetModel tweetModel) {
		List<T> ret = new ArrayList<T>();
		
		for (int len = offset; len <= size; len++) {
			String prefix = new StringBuilder().append(len).append("OF").append(size).toString();
			
			for (int i = 0; i < ordered.size(); i++) {
				String key = ordered.get(i);
				if (tweetModel.stop(key)) continue;
				
				if (len - 1 == 0) {
					TokenPermutation<T> permutation = construct(value, key, grid.get(key));
					ret.addAll(permutation.reduce(prefix));
					continue;
				}
				
				int start = Math.max(0, i - radius);
				int end = Math.min(ordered.size(), i + radius);				
				int layers = len - 1;
				
				int[] offsets = new int[layers];
				for (int l = 0; l < layers; l++) offsets[l] = 0;
				
				HashSet<Integer> seenSet = new HashSet<Integer>();
				ArrayDeque<Integer> seenStack = new ArrayDeque<Integer>();

				if (i == start) {
					++start;
				} else if (i == end) {
					--end;
				}

				boolean done = false;
				while (!done) {					
					for (int k = start; k < end; k++) {						
						offsets[seenStack.size()] = k;
						
						if (!seenSet.add(k)) continue;
						if (k == i) continue;		
						seenStack.add(k);
						
						if (seenStack.size() == layers) {
							seenSet.remove(seenStack.remove());
							
							TokenPermutation<T> permutation = construct(value, key, grid.get(key));
							
							for (int o = 0; o < offsets.length; o++) {
								String mod = ordered.get(offsets[o]);
								permutation.addDimension(value, mod, offsets[o] - i, offsets[o] < i ? TokenAlignment.PRE : TokenAlignment.POST, grid.get(mod));
							}
							
							ret.addAll(permutation.reduce(prefix));
						}
					}
					
					int reset = -1;
					for (int o = 0; o < offsets.length; o++) {
						if (reset > 1) {
							// this is going to be moving back to front
							// while o is going from front to back, but
							// the count is all that matters (which must account
							// for the fact that we already pop off the end
							// element)
							seenSet.remove(seenStack.remove());
						}
						
						if (offsets[o] == end - 1) {							
							reset = 0;
							
							if (o > 0) {
								offsets[o - 1]++;
							} else {
								done = true;
								break;
							}
						}
						
						if (reset > -1) {
							offsets[o] = 0;
							++reset;
						}
					}
				}
			}
		}
		
		return ret;
	}
	
	public void addDimension(String value, String key, int distance, TokenAlignment alignment, List<TokenWordType> types) {
		List<T> newpermutations = new ArrayList<T>(this.permutations.size() * types.size());
		
		for (TokenWordType type: types) {
			for (T permutation: this.permutations) {
				newpermutations.add(this.add(this.copy(permutation), key, distance, alignment, type));
			}
		}
		
		this.permutations = newpermutations;
	}

	protected abstract T copy(T permutation);
	protected abstract T add(T permutation, String key, int distance, TokenAlignment alignment, TokenWordType type);
	
	public abstract List<T> reduce(String prefix);
	
	public abstract TokenPermutation<T> construct(String value, String key, List<TokenWordType> types);
	public abstract TokenPermutation<T> copy();
	
}
