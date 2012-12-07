package net.vurs.service.definition.nlp.regex;

public class RegexStrings {
	
	public static class TwitterSpecific {
		public static String HASHTAG_CHARACTERS = "[a-zA-Z0-9_\\u00c0-\\u00d6\\u00d8-\\u00f6\\u00f8-\\u00ff]";
		public static String HASHTAGS = String.format("(^|[^0-9A-Za-z&/]+)(#|\uFF03)([0-9A-Za-z_]*[A-Za-z_]+%s*)", TwitterSpecific.HASHTAG_CHARACTERS);
		
		public static String AT_SIGNS = "[@\uFF20]";
		public static String USERNAMES = String.format("([^a-zA-Z0-9_]|^)(%s+)([a-zA-Z0-9_]{1,20})", TwitterSpecific.AT_SIGNS);
		public static String USERNAMES_AND_LISTS = String.format("%s(/[a-zA-Z][a-zA-Z0-9\\x80-\\xFF-]{0,79})?", TwitterSpecific.USERNAMES);
	}
	
	public static class CharacterSets {
		public static String PUNCTUATION = "['рс\".?!,:;]";
		public static String HTMLENTITY = "&(amp|lt|gt|quot);";
		public static String PHRASE_SEPARATORS = RegexUtil.union("-+", "\\.{2,}");
	}
	
	public static class Numerics {
		public static String TIME = "\\d+:\\d+";
		public static String NUMBER_WITH_DECIMAL = "\\d+\\.\\d+";
		public static String NUMBER_WITH_COMMAS = String.format("(\\d+,)+?\\d{3}%s", RegexUtil.lookaheadPositive(RegexUtil.union("[^,]", "$")));
	}
	
	public static class URL {
		public static String LINK = new StringBuilder("(https?://)")
                .append("(([0-9a-z_!~*'().&=+$%-]+: )?[0-9a-z_!~*'().&=+$%-]+@)?") //user@ 
                .append("(([0-9]{1,3}\\.){3}[0-9]{1,3}") //IP- 199.194.52.184 
                .append("|") //allows either IP or domain 
                .append("(([0-9a-z_!~*'()-]+\\.))*") //tertiary domain(s)- www. 
                .append("([0-9a-z][0-9a-z-]{0,61})?[0-9a-z]\\.") //second level domain 
                .append("[a-z]{2,6})") //first level domain- .com or .museum 
                .append("(:[0-9]{1,4})?") //port number- :80 
                .append("((/[0-9a-zA-Z_!~*'().;?:@&=+$,%#-]+)+/?)*").toString();
	}
	
	public static class Abbreviations {
		public static String STANDARD = null;
		static {
			String[] abbrevs = new String[]{"am","pm","us","usa","ie","eg"};
			StringBuilder builder = new StringBuilder();
			for (String abbrev: abbrevs) {
				builder.append(RegexUtil.ignoreCase(abbrev)).append(RegexUtil.optional("[.:]"));
			}
			STANDARD = builder.toString();
		}
		
		public static String ARBITRARY = null;
		static {
			String boundary = RegexUtil.union("\\s", CharacterSets.PUNCTUATION.replace(".", ""), CharacterSets.HTMLENTITY);
			String solid = String.format("([A-Za-z]\\.?){2,}%s", RegexUtil.lookaheadPositive(boundary));
			String dotted = String.format("([A-Za-z]\\.?){1,}[A-Za-z]%s", RegexUtil.lookaheadPositive(boundary));
			ARBITRARY = RegexUtil.union(solid, dotted);
		}
	}
	
	public static class Emoticons {
		public static class Components {
			public static class Eyes {
				public static String NORMAL = "[:=]";
				public static String WINK = "[;]";
			}
			
			public static String NOSE = RegexUtil.union("", "o","O","-");
			
			public static class Mouth {
				public static String HAPPY = "[D\\)\\]]";
				public static String SAD = "[\\(\\[]";
				public static String TONGUE = "[pP]";
				public static String OTHER = "[sSdoO/\\\\]";
			}
		}
		
		public static String HAPPY = RegexUtil.union("\\^_\\^", String.format("%s%s%s", Components.Eyes.NORMAL, Components.NOSE, Components.Mouth.HAPPY));
		public static String SAD = RegexUtil.union("\\-_\\-", String.format("%s%s%s", Components.Eyes.NORMAL, Components.NOSE, Components.Mouth.SAD));
		public static String WINK = String.format("%s%s%s", Components.Eyes.WINK, Components.NOSE, Components.Mouth.HAPPY);
		public static String CRY = String.format("%s%s%s", Components.Eyes.WINK, Components.NOSE, Components.Mouth.SAD);
		public static String TONGUE = String.format("%s%s%s", Components.Eyes.NORMAL, Components.NOSE, Components.Mouth.TONGUE);
		public static String OTHER = String.format("%s%s%s", RegexUtil.union(Components.Eyes.NORMAL, Components.Eyes.WINK), Components.NOSE, Components.Mouth.OTHER);
		public static String ANY = String.format("%s%s%s", RegexUtil.union(Components.Eyes.NORMAL, Components.Eyes.WINK), Components.NOSE, RegexUtil.union(Components.Mouth.HAPPY, Components.Mouth.SAD, Components.Mouth.TONGUE, Components.Mouth.OTHER));
	}
		
	public static String COMMON = RegexUtil.union(
			URL.LINK,
			Emoticons.ANY,
			TwitterSpecific.USERNAMES_AND_LISTS,
			TwitterSpecific.HASHTAGS,
			
			CharacterSets.HTMLENTITY, 
			CharacterSets.PHRASE_SEPARATORS,
			CharacterSets.PUNCTUATION, 
			
			Numerics.NUMBER_WITH_COMMAS, 
			Numerics.NUMBER_WITH_DECIMAL, 
			Numerics.TIME,
			
			Abbreviations.ARBITRARY, 
			Abbreviations.STANDARD
		);
	
	public static class Edges {
		public static String EDGE_PUNCTUATION = "[ ' \" р с т у < > г х { } \\( \\) \\[ \\]  ]".replace(" ","");
		public static String NON_EDGE_PUNCTUATION = "[A-Za-z0-9]";
		public static String LEFT = String.format("(\\s|^)(%s+)(%s)", EDGE_PUNCTUATION, NON_EDGE_PUNCTUATION);
		public static String RIGHT = String.format("(%s)(%s+)(\\s|$)", NON_EDGE_PUNCTUATION, EDGE_PUNCTUATION);
	}
	
	public static class Spaces {
		public static String SPACES = new StringBuilder("[")
				.append("\\u0009-\\u000d")      //  # White_Space # Cc   [5] <control-0009>..<control-000D>
				.append("\\u0020")              // White_Space # Zs       SPACE
				.append("\\u0085")              // White_Space # Cc       <control-0085>
				.append("\\u00a0")              // White_Space # Zs       NO-BREAK SPACE
				.append("\\u1680")              // White_Space # Zs       OGHAM SPACE MARK
				.append("\\u180E")              // White_Space # Zs       MONGOLIAN VOWEL SEPARATOR
				.append("\\u2000-\\u200a")      // # White_Space # Zs  [11] EN QUAD..HAIR SPACE
				.append("\\u2028")              // White_Space # Zl       LINE SEPARATOR
				.append("\\u2029")              // White_Space # Zp       PARAGRAPH SEPARATOR
				.append("\\u202F")              // White_Space # Zs       NARROW NO-BREAK SPACE
				.append("\\u205F")              // White_Space # Zs       MEDIUM MATHEMATICAL SPACE
				.append("\\u3000")              // White_Space # Zs       IDEOGRAPHIC SPACE)
				.append("]").toString();
	}
	
}
