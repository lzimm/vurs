package net.vurs.conn.hbase.typesafety.keytypes;

import net.vurs.conn.hbase.streamer.HBaseColumnIndexStreamer;

public interface ColumnIndexType<T, C extends HBaseColumnIndexStreamer<T>> {

}
