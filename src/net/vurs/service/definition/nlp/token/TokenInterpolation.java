package net.vurs.service.definition.nlp.token;

import java.util.ArrayList;
import java.util.List;

import net.vurs.common.Quad;
import net.vurs.service.definition.nlp.token.enumeration.TokenAlignment;
import net.vurs.service.definition.nlp.token.enumeration.TokenWordType;

public class TokenInterpolation {

	private String prefix = null;
	private String value = null;
	private List<Quad<String, Integer, TokenAlignment, TokenWordType>> modifiers = null;
	
	public TokenInterpolation(String value) {
		this(value, null);
	}
	
	public boolean containsValue() {
		for (Quad<String, Integer, TokenAlignment, TokenWordType> t: modifiers) {
			if (t.a().equals(this.value)) return true;
		}
		
		return false;
	}
	
	public TokenInterpolation(String value, List<Quad<String, Integer, TokenAlignment, TokenWordType>> modifiers) {
		this.value = value;
		if (modifiers == null) {
			this.modifiers = new ArrayList<Quad<String, Integer, TokenAlignment, TokenWordType>>();
		} else {
			this.modifiers = new ArrayList<Quad<String, Integer, TokenAlignment, TokenWordType>>(modifiers);
		}
	}
	
	public void addModifier(String modifier, int distance, TokenAlignment alignment, TokenWordType wordType) {
		this.modifiers.add(new Quad<String, Integer, TokenAlignment, TokenWordType>(modifier, distance, alignment, wordType));
	}
	
	public String getValue() { return this.value; }
	public List<Quad<String, Integer, TokenAlignment, TokenWordType>> getModifiers() { return this.modifiers; }
	
	public String getPhrase() {
		StringBuilder ret = new StringBuilder();

		if (this.prefix != null) {
			ret.append("(PREFIX:").append(this.prefix).append(')');
		}
		
		for (Quad<String, Integer, TokenAlignment, TokenWordType> modifier: modifiers) {
			ret.append('(').append(modifier.d()).append(':').append(modifier.c()).append('/').append(modifier.b()).append(':').append(modifier.a().equals(this.value) ? "(VALUE)" : modifier.a()).append(')');
		}
		
		return ret.toString();
	}
	
	public TokenInterpolation copy() {
		return new TokenInterpolation(this.value, this.modifiers);
	}
	
	@Override
	public String toString() {
		return getPhrase();
	}

	public void setPrefix(String prefix) {
		this.prefix = prefix;
	}
	
}
