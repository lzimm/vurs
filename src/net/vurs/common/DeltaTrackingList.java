package net.vurs.common;

import java.util.ArrayList;
import java.util.List;

public class DeltaTrackingList<T> extends ArrayList<T> {
	private static final long serialVersionUID = 6413907529280790395L;
	private static enum TYPE { ADD, REMOVE };
	
	private List<TimeUUID> uuids = null;
	private List<Triple<TYPE, TimeUUID, T>> deltas = null; 
	
	public DeltaTrackingList() {
		super();
		this.uuids = new ArrayList<TimeUUID>();
		this.mark();
	}
	
	public TimeUUID id(int i) {
		return this.uuids.get(i);
	}
	
	@Override
	public T set(int i, T o) {
		return this.set(i, o, new TimeUUID());
	}
	
	public T set(int i, T o, TimeUUID id) {
		TimeUUID rid = this.uuids.set(i, id);
		T r = super.set(i, o);
		
		if (r != null) {
			this.deltas.add(new Triple<TYPE, TimeUUID, T>(TYPE.REMOVE, rid, r));
		}
		
		this.deltas.add(new Triple<TYPE, TimeUUID, T>(TYPE.ADD, id, o));
		return r;		
	}
	
	@Override
	public void add(int i, T o) {
		this.add(i, o, new TimeUUID());
	}
	
	public void add(int i, T o, TimeUUID id) {
		this.uuids.add(i, id);
		super.add(i, o);
		
		this.deltas.add(new Triple<TYPE, TimeUUID, T>(TYPE.ADD, id, o));
	}

	@Override
	public boolean add(T o) {
		return this.add(o, new TimeUUID());
	}
	
	public boolean add(T o, TimeUUID id) {
		boolean rid = this.uuids.add(id);
		boolean r = super.add(o);
		
		if (rid && r) {
			this.deltas.add(new Triple<TYPE, TimeUUID, T>(TYPE.ADD, id, o));
		}
		
		return rid && r;
	}

	public void mark() {
		this.deltas = new ArrayList<Triple<TYPE, TimeUUID, T>>();
	}
	
	public List<Triple<TYPE, TimeUUID, T>> getChanges() {
		return this.deltas;
	}
	
	public List<Pair<TimeUUID, T>> getAdditions() {
		List<Pair<TimeUUID, T>> ret = new ArrayList<Pair<TimeUUID, T>>(this.deltas.size());
		for (Triple<TYPE, TimeUUID, T> triple: this.deltas) {
			if (triple.a().equals(TYPE.ADD)) {
				ret.add(new Pair<TimeUUID, T>(triple.b(), triple.c()));
			}
		}
		return ret;
	}
	
	public List<Pair<TimeUUID, T>> getRemovals() {
		List<Pair<TimeUUID, T>> ret = new ArrayList<Pair<TimeUUID, T>>(this.deltas.size());
		for (Triple<TYPE, TimeUUID, T> triple: this.deltas) {
			if (triple.a().equals(TYPE.REMOVE)) {
				ret.add(new Pair<TimeUUID, T>(triple.b(), triple.c()));
			}
		}
		return ret;
	}

}
