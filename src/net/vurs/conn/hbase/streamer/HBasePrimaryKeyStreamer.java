package net.vurs.conn.hbase.streamer;

public interface HBasePrimaryKeyStreamer<T> {
	
	public String generateKey();
	
}
