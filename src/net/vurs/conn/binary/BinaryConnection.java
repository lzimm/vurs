package net.vurs.conn.binary;

import net.vurs.conn.Connection;

public class BinaryConnection extends Connection {	
	
    public BinaryConnection(String poolKey) {
    	super(poolKey);
	}
	
	@Override
	protected void close() {
	}
	
}
