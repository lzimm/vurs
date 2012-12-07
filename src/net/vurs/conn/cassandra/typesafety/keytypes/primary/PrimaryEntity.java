package net.vurs.conn.cassandra.typesafety.keytypes.primary;

import net.vurs.conn.cassandra.streamer.component.EntityStreamer;
import net.vurs.conn.cassandra.typesafety.keytypes.CassandraPrimaryKeyType;
import net.vurs.entity.Definition;
import net.vurs.entity.Entity;

public class PrimaryEntity<T extends Definition<?>> implements CassandraPrimaryKeyType<Entity<T>, EntityStreamer<T>> {

}
