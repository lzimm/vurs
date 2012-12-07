package net.vurs.service.definition.nlp.token;

import java.util.ArrayList;
import java.util.List;

public class Tokenization {
	
	private String text = null;
	private List<String> tokens = null;
	private List<Integer> alignments = null;
	
	public Tokenization(String text, List<String> tokens, List<Integer> alignments) {
		this.text = text;
		this.tokens = tokens;
		this.alignments = alignments;
	}
	
	public Tokenization subset(Integer... ids) {
		List<String> subTokens = new ArrayList<String>(ids.length);
		List<Integer> subAlignments = new ArrayList<Integer>(ids.length);
		
		for (Integer id: ids) {
			subTokens.add(this.tokens.get(id));
			subAlignments.add(this.alignments.get(id));
		}
		
		return new Tokenization(this.text, subTokens, subAlignments);
	}
	
	public Tokenization subsetRange(Integer start, Integer finish) {
		List<String> subTokens = new ArrayList<String>(finish - start);
		List<Integer> subAlignments = new ArrayList<Integer>(finish - start);
		
		for (Integer i = start; i < finish; i++) {
			subTokens.add(this.tokens.get(i));
			subAlignments.add(this.alignments.get(i));
		}
		
		return new Tokenization(this.text, subTokens, subAlignments);
	}
	
	public List<Tokenization> skipGram(Integer skip) {
		int n = skip + 2;
		int len = tokens.size() - n;
		if (len < 1) return new ArrayList<Tokenization>();
		
		ArrayList<Tokenization> ret = new ArrayList<Tokenization>(len);
		
		for (int i = 0; i < len; i++) {
			ret.add(subset(i, i + skip + 1));
		}
		
		return ret;
	}
	
	public List<Tokenization> ngrams(Integer... grams) {
		int size = tokens.size();
		int len = Integer.MAX_VALUE;
		int total = 0;
		
		for (Integer n: grams) {
			total += Math.max(0, size - (n - 1));
			if (n < len) {
				len = n;
			}
		}
		
		len = tokens.size() - (len - 1);
		
		if (len < 1) return new ArrayList<Tokenization>();
		
		ArrayList<Tokenization> ret = new ArrayList<Tokenization>(total);
		
		for (int i = 0; i < len; i++) {
			for (Integer n: grams) {
				if (i < size - (n - 1)) {
					Tokenization range = subsetRange(i, i + n);
					ret.add(range);
				}
			}
		}
		
		return ret;
	}
	
	public String text() { return this.text; }
	public List<String> tokens() { return this.tokens; }
	public List<Integer> alignments() { return this.alignments; }
	public String token(int i) { return this.tokens.get(i); }
	
	@Override
	public int hashCode() {
		return this.tokens.hashCode();
	}
	
	@Override
	public String toString() {
		return String.format("tokens (%s), alignments (%s)", this.tokens, this.alignments);
	}
	
	@Override
	public boolean equals(Object o) {
		return o.getClass().equals(Tokenization.class) && this.tokens().equals(((Tokenization) o).tokens);
	}
	
}
