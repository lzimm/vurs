package net.vurs.conn.cassandra.streamer;

public interface CassandraPrimaryKeyStreamer<T> {
	
	public String generateKey();
	
}
