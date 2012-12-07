package net.vurs.conn.cassandra.typesafety.keytypes.primary;

import net.vurs.common.TimeUUID;
import net.vurs.conn.cassandra.streamer.component.TimeUUIDStreamer;
import net.vurs.conn.cassandra.typesafety.keytypes.CassandraPrimaryKeyType;

public class PrimaryTimeUUID implements CassandraPrimaryKeyType<TimeUUID, TimeUUIDStreamer> {

}
