package net.vurs.service.definition.nlp.token;

import java.util.ArrayList;
import java.util.List;

import net.vurs.common.Quad;
import net.vurs.service.definition.nlp.token.enumeration.TokenAlignment;
import net.vurs.service.definition.nlp.token.enumeration.TokenWordType;

public class TokenPhrase {

	private String prefix = null;
	private String verb = null;
	private List<Quad<String, Integer, TokenAlignment, TokenWordType>> modifiers = null;
	
	public TokenPhrase() {
		this(null);
	}
	
	public TokenPhrase(String verb) {
		this(verb, null);
	}
	
	public TokenPhrase(String verb, List<Quad<String, Integer, TokenAlignment, TokenWordType>> modifiers) {
		this.verb = verb;
		if (modifiers == null) {
			this.modifiers = new ArrayList<Quad<String, Integer, TokenAlignment, TokenWordType>>();
		} else {
			this.modifiers = new ArrayList<Quad<String, Integer, TokenAlignment, TokenWordType>>(modifiers);
		}
	}
	
	public void addModifier(String modifier, int distance, TokenAlignment alignment, TokenWordType wordType) {
		this.modifiers.add(new Quad<String, Integer, TokenAlignment, TokenWordType>(modifier, distance, alignment, wordType));
	}
	
	public String getVerb() { return this.verb; }
	public List<Quad<String, Integer, TokenAlignment, TokenWordType>> getModifiers() { return this.modifiers; }
	
	public String getPhrase() {
		StringBuilder ret = new StringBuilder();

		if (this.prefix != null) {
			ret.append("(PREFIX:").append(this.prefix).append(')');
		}
		
		if (this.verb != null) {
			ret.append("(VERB:").append(this.verb).append(')');
		}
		
		for (Quad<String, Integer, TokenAlignment, TokenWordType> modifier: modifiers) {
			ret.append('(').append(modifier.d()).append(':').append(modifier.c()).append('/').append(modifier.b()).append(':').append(modifier.a()).append(')');
		}
		
		return ret.toString();
	}
	
	public TokenPhrase copy() {
		return new TokenPhrase(this.verb, this.modifiers);
	}
	
	@Override
	public String toString() {
		return getPhrase();
	}

	public void setPrefix(String prefix) {
		this.prefix = prefix;
	}
	
}
