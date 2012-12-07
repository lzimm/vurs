package net.vurs.service.definition.nlp.regex;

public class RegexUtil {

	public static String union(String... patterns) {
		StringBuilder builder = new StringBuilder("(");
		for (String pattern: patterns) {
			builder.append(pattern).append("|");
		}
		return builder.deleteCharAt(builder.length() - 1).append(")").toString();
	}
	
	public static String ignoreCase(Character ch) {
		return String.format("[%s%s]", Character.toLowerCase(ch), Character.toUpperCase(ch));
	}
	
	public static String ignoreCase(String word) {
		StringBuilder builder = new StringBuilder();
		for (int i = 0; i < word.length(); i++) {
			builder.append(ignoreCase(word.charAt(i)));
		}
		return builder.toString();
	}
	
	public static String optional(String pattern) {
		return String.format("(%s)?", pattern);
	}
	
	public static String lookaheadPositive(String pattern) {
		return String.format("(?=%s)", pattern);
	}
	
	public static String lookaheadNegative(String pattern) {
		return String.format("(?!%s)", pattern);
	}
	
}
