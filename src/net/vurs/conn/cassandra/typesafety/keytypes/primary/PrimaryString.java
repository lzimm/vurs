package net.vurs.conn.cassandra.typesafety.keytypes.primary;

import net.vurs.conn.cassandra.streamer.component.StringStreamer;
import net.vurs.conn.cassandra.typesafety.keytypes.CassandraPrimaryKeyType;

public class PrimaryString implements CassandraPrimaryKeyType<String, StringStreamer> {

}
