package net.vurs.common;

import java.util.ArrayList;
import java.util.List;
import java.util.TreeMap;
import java.util.Map.Entry;

import net.vurs.common.constructor.ArrayListConstructor;

public class Ranking<T> {

	private Integer totalCount = null;
	private TreeMap<Float, List<T>> ranking = null;
	
	public Ranking() {
		this.totalCount = 0;
		this.ranking = new ConstructingTreeMap<Float, List<T>>(new ArrayListConstructor<Float, T>());
	}
	
	public void add(Float score, T el) {
		this.totalCount += 1;
		this.ranking.get(score).add(el);
	}
	
	public List<T> ranked() { return ranked(0); }
	
	public List<T> ranked(int count) {
		List<T> ret = new ArrayList<T>(this.totalCount);
		
		for (Entry<Float, List<T>> e: this.ranking.descendingMap().entrySet()) {
			for (T el: e.getValue()) {
				ret.add(el);
				
				if (count > 0 && ret.size() == count) {
					return ret;
				}
			}
		}
		
		return ret;
	}
	
}
