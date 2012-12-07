package net.vurs.util;

import java.util.HashSet;

@SuppressWarnings("serial")
public class EscapeUtil {

	public static final Character BACKSLASH = '\\';
	
	public static final HashSet<Character> MYSQL_CHARS = new HashSet<Character>() {{
		add('"');
		add('\'');
		add('\\');
		add(';');
	}};
	
	public static final String escape(String text, Character escapeWith, HashSet<Character> escapeChars) {
		StringBuilder b = new StringBuilder(text.length());
		
		for (Character c: text.toCharArray()) {
			if (escapeChars.contains(c)) {
				b.append(escapeWith);
			}
			
			b.append(c);
		}
		
		return b.toString();
	}
	
}
