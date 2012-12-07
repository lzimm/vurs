package net.vurs.service.definition.cluster;

import java.net.InetAddress;
import java.util.concurrent.atomic.AtomicReference;

import net.vurs.service.definition.cluster.layer.ClusterLayer;

import org.getopt.util.hash.MurmurHash;

public class ClusterRing {
	private final int INITIAL_RING_SIZE = 12;
	private final int POSITIONS_PER_NODE = 3;
	private final float MIN_DENSITY = 0.25f;
	private final float MAX_DENSITY = 0.90f;
	
	private final int KEYSPACE = 2000000;
	private final float KEYFLOAT = KEYSPACE*1.0F;
	
	private Class<? extends ClusterLayer> layer = null;
	private AtomicReference<InetAddress[]> ringRef = null;
	
	public ClusterRing(Class<? extends ClusterLayer> layer) {
		InetAddress[] initialRing = new InetAddress[INITIAL_RING_SIZE];
		
		this.layer = layer;
		this.ringRef = new AtomicReference<InetAddress[]>();		
		this.ringRef.set(initialRing);
	}
	
	public void add(InetAddress address) {
		InetAddress[] oldRing = this.ringRef.get();
		InetAddress[] newRing = new InetAddress[oldRing.length];
		System.arraycopy(oldRing, 0, newRing, 0, oldRing.length);
		
		int i = POSITIONS_PER_NODE;
		while (i-- > 0) {
			newRing = add(address, newRing, i);
		}
		
		if (!this.ringRef.compareAndSet(oldRing, newRing)) {
			add(address);
		}
	}
	
	private InetAddress[] add(InetAddress address, InetAddress[] ring, int count) {
		if ((countElements(ring) + count)*1.0f / ring.length*1.0f > MAX_DENSITY) {
			ring = rescale(ring, 2f);
		}
		
		int targetPosition = Math.abs(MurmurHash.hash(address.getAddress(), count)) % KEYSPACE;
		while (ring[targetPosition++ % ring.length] != null) continue;
		ring[--targetPosition % ring.length] = address;
		
		return ring;
	}

	public void remove(InetAddress address) {
		InetAddress[] oldRing = this.ringRef.get();
		InetAddress[] newRing = new InetAddress[oldRing.length];
		System.arraycopy(oldRing, 0, newRing, 0, oldRing.length);
		
		for (int i = 0; i < newRing.length; i++) {
			InetAddress addr = newRing[i];
			if (addr != null && addr.getHostAddress().equals(address.getHostAddress())) {
				newRing[i] = null;
			}
		}
		
		if (countElements(newRing)*1.0f / newRing.length*1.0f < MIN_DENSITY) {
			newRing = rescale(newRing, 0.5f);
		}
		
		if (!this.ringRef.compareAndSet(oldRing, newRing)) {
			remove(address);
		}
	}
	
	private InetAddress[] rescale(InetAddress[] ring, float scale) {
		int newSize = (int) Math.ceil(ring.length * scale);
		InetAddress[] newRing = new InetAddress[newSize];
		
		for (int i = 0; i < ring.length; i++) {
			InetAddress addr = ring[i];
			if (addr != null) {
				int newPos = (int) Math.floor(i * scale);
				while (newRing[newPos++ % newRing.length] != null) continue;
				newRing[--newPos % newRing.length] = addr;
			}
		}
		
		return newRing;
	}
	
	private int countElements(InetAddress[] ring) {
		int count = 0;
		for (int i = 0; i < ring.length; i++) {
			if (ring[i] != null) count++;
		}
		return count;
	}
	
	public InetAddress findPosition(int key) {
		key = Math.abs(key) % KEYSPACE;
		InetAddress[] ring = this.ringRef.get();
		int idx = (int) Math.floor((key*1.0f / KEYFLOAT) * ring.length * 1.0f);
		while (ring[idx++ % ring.length] == null) continue;
		return ring[--idx % ring.length];
	}
	
	public Class<? extends ClusterLayer> getLayer() { return this.layer; }

	public void clear() {
		this.ringRef.set(new InetAddress[INITIAL_RING_SIZE]);
	}

}
