package net.vurs.conn.cache;

import net.vurs.conn.Connection;
import net.vurs.util.ErrorControl;

public class CacheConnection extends Connection {
	
    public CacheConnection(String poolKey) {
    	super(poolKey);
		setupTransport();
	}

	private void setupTransport() {        
        try {
		} catch (Exception e) {
			ErrorControl.logException(e);
		}
    }
	
	@Override
	protected void close() {
	}
	
}
