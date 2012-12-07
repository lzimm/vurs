package net.vurs.conn.hbase.typesafety.keytypes.primary;

import net.vurs.conn.hbase.streamer.component.StringStreamer;
import net.vurs.conn.hbase.typesafety.keytypes.CassandraPrimaryKeyType;

public class PrimaryString implements CassandraPrimaryKeyType<String, StringStreamer> {

}
