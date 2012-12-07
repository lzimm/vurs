package net.vurs.conn.cassandra.typesafety.keytypes;

import net.vurs.conn.cassandra.streamer.CassandraPrimaryKeyStreamer;
import net.vurs.entity.typesafety.PrimaryKeyType;

public interface CassandraPrimaryKeyType<T, P extends CassandraPrimaryKeyStreamer<T>> extends PrimaryKeyType<T> {

}
