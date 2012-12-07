package net.vurs.conn.cassandra.typesafety.keytypes.primary;

import net.vurs.conn.cassandra.streamer.component.LongStreamer;
import net.vurs.conn.cassandra.typesafety.keytypes.CassandraPrimaryKeyType;

public class PrimaryLong implements CassandraPrimaryKeyType<Long, LongStreamer> {

}
