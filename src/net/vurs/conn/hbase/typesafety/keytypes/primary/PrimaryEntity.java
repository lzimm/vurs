package net.vurs.conn.hbase.typesafety.keytypes.primary;

import net.vurs.conn.hbase.streamer.component.EntityStreamer;
import net.vurs.conn.hbase.typesafety.keytypes.CassandraPrimaryKeyType;
import net.vurs.entity.Definition;
import net.vurs.entity.Entity;

public class PrimaryEntity<T extends Definition<?>> implements CassandraPrimaryKeyType<Entity<T>, EntityStreamer<T>> {

}
