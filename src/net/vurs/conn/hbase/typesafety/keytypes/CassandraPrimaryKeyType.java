package net.vurs.conn.hbase.typesafety.keytypes;

import net.vurs.conn.hbase.streamer.HBasePrimaryKeyStreamer;
import net.vurs.entity.typesafety.PrimaryKeyType;

public interface CassandraPrimaryKeyType<T, P extends HBasePrimaryKeyStreamer<T>> extends PrimaryKeyType<T> {

}
