package net.vurs.entity;

public abstract class ComponentStreamer<C, I, O> {

	public abstract O write(Object comp, byte[] name, long ts);
	public abstract C read(I data);
	
}
