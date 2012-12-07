package net.vurs.common;

import java.util.concurrent.ConcurrentLinkedQueue;

import net.vurs.common.constructor.ConcurrentLinkedQueueConstructor;

public class ConcurrentLinkedQueueMap<K, V> extends ConcurrentConstructingHashMap<K, ConcurrentLinkedQueue<V>> {
	private static final long serialVersionUID = 5014867946954589361L;

	public ConcurrentLinkedQueueMap() {
		super(new ConcurrentLinkedQueueConstructor<K, V>());
	}

}
