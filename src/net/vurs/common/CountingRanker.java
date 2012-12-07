package net.vurs.common;

import java.util.Map.Entry;
import java.util.concurrent.atomic.AtomicLong;

import net.vurs.common.constructor.AtomicLongConstructor;

public class CountingRanker<T> {

	private ConstructingHashMap<T, AtomicLong> counter = null;

	public CountingRanker() {
		this.counter  = new ConstructingHashMap<T, AtomicLong>(new AtomicLongConstructor<T>());
	}
	
	public long count(T el) {
		return this.counter.get(el).getAndIncrement();
	}
	
	public long count(T el, long delta) {
		return this.counter.get(el).addAndGet(delta);
	}
	
	public Ranking<T> ranking() {
		Ranking<T> ret = new Ranking<T>();
		
		for (Entry<T, AtomicLong> e: this.counter.entrySet()) {
			ret.add(e.getValue().floatValue(), e.getKey());
		}
		
		return ret;
	}
	
}
