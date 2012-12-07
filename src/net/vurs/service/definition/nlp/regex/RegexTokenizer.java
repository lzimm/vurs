package net.vurs.service.definition.nlp.regex;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import net.vurs.common.Pair;
import net.vurs.service.definition.nlp.token.Tokenization;

public class RegexTokenizer {

	private Edges edges = new Edges();
	private class Edges {
		public Pattern LEFT = Pattern.compile(RegexStrings.Edges.LEFT);
		public Pattern RIGHT = Pattern.compile(RegexStrings.Edges.RIGHT);
		
		public String mungeLeft(String input) {
			return LEFT.matcher(input).replaceAll("$1$2 $3");
		}
		
		public String mungeRight(String input) {
			return RIGHT.matcher(input).replaceAll("$1 $2$3");
		}
		
		public String munge(String input) {
			return mungeRight(mungeLeft(input));
		}
	}
	
	private Whitespace whitespace = new Whitespace();
	private class Whitespace {
		public Pattern WHITESPACE = Pattern.compile("\\s+");
		public Pattern SPACES = Pattern.compile(RegexStrings.Spaces.SPACES);
		
		public String squeeze(String input) {
			return WHITESPACE.matcher(input).replaceAll(" ").trim();
		}
	}
	
	private Tokenizer tokenizer = new Tokenizer();
	private class Tokenizer {
		public Pattern COMMON_TOKENS = Pattern.compile(RegexStrings.COMMON);
		
		public Pair<List<String>, List<Integer>> aligned(String input) {
			input = edges.munge(input);
			
			ArrayList<Pair<Integer, Integer>> gaps = new ArrayList<Pair<Integer, Integer>>();
			ArrayList<Pair<Integer, Integer>> matches = new ArrayList<Pair<Integer, Integer>>();
			
			Integer p = 0;
			
			Matcher m = COMMON_TOKENS.matcher(input);
			while (m.find()) {
				gaps.add(new Pair<Integer, Integer>(p, m.start()));
				matches.add(new Pair<Integer, Integer>(m.start(), m.end()));
				p = m.end();
			}
			
			gaps.add(new Pair<Integer, Integer>(p, input.length()));
			
			ArrayList<String> tokens = new ArrayList<String>();
			ArrayList<Integer> alignments = new ArrayList<Integer>();
			
			for (int i = 0; i < matches.size(); i++) {
				Pair<Integer, Integer> gap = gaps.get(i);
				String gapToken = input.substring(gap.a(), gap.b());
				if (gapToken.length() > 1 || (gapToken.length() == 1 && !whitespace.SPACES.matcher(gapToken).matches())) {
					tokens.add(gapToken.trim().toLowerCase());
					alignments.add(gap.a());
				}
				
				Pair<Integer, Integer> match = matches.get(i);
				String matchToken = input.substring(match.a(), match.b());
				if (matchToken.length() > 1 || (matchToken.length() == 1 && !whitespace.SPACES.matcher(matchToken).matches())) {
					tokens.add(matchToken.trim().toLowerCase());
					alignments.add(match.a());
				}
			}
			
			Pair<Integer, Integer> gap = gaps.get(gaps.size() - 1);
			String gapToken = input.substring(gap.a(), gap.b());
			if (gapToken.length() > 1 || (gapToken.length() == 1 && !whitespace.SPACES.matcher(gapToken).find())) {
				tokens.add(gapToken.trim().toLowerCase());
				alignments.add(gap.a());
			}
			
			return new Pair<List<String>, List<Integer>>(tokens, alignments);
		}
	}
	
	public Tokenization tokenize(String input) {
		String text = whitespace.squeeze(input);
		Pair<List<String>, List<Integer>> tokens = tokenizer.aligned(text);
		return new Tokenization(text, tokens.a(), tokens.b());
	}
	
}
