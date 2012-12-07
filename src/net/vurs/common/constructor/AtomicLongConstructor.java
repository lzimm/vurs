package net.vurs.common.constructor;

import java.util.concurrent.atomic.AtomicLong;

import net.vurs.common.functional.F1;

public class AtomicLongConstructor<K> implements F1<K, AtomicLong> {

	@Override
	public AtomicLong f(K a) {
		return new AtomicLong(0);
	}

}
