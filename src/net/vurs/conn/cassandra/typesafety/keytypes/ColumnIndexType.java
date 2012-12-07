package net.vurs.conn.cassandra.typesafety.keytypes;

import net.vurs.conn.cassandra.streamer.CassandraColumnIndexStreamer;

public interface ColumnIndexType<T, C extends CassandraColumnIndexStreamer<T>> {

}
