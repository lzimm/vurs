package net.vurs.common.constructor;

import java.util.concurrent.ConcurrentLinkedQueue;

import net.vurs.common.functional.F1;

public class ConcurrentLinkedQueueConstructor<K, T> implements F1<K, ConcurrentLinkedQueue<T>> {

	@Override
	public ConcurrentLinkedQueue<T> f(K a) {
		return new ConcurrentLinkedQueue<T>();
	}

}
