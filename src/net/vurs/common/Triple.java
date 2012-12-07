package net.vurs.common;

public class Triple<A, B, C> {

	private A a;
	private B b;
	private C c;
	
	public Triple(A a, B b, C c) {
		this.a = a;
		this.b = b;
		this.c = c;
	}
	
	public A a() { return a; }
	public B b() { return b; }
	public C c() { return c; }
	
}
