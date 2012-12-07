package net.vurs.conn.hbase.typesafety.keytypes.primary;

import net.vurs.common.TimeUUID;
import net.vurs.conn.hbase.streamer.component.TimeUUIDStreamer;
import net.vurs.conn.hbase.typesafety.keytypes.CassandraPrimaryKeyType;

public class PrimaryTimeUUID implements CassandraPrimaryKeyType<TimeUUID, TimeUUIDStreamer> {

}
