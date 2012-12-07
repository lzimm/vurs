package net.vurs.conn.cassandra.typesafety.keytypes.column;

import net.vurs.common.TimeUUID;
import net.vurs.conn.cassandra.streamer.component.TimeUUIDStreamer;
import net.vurs.conn.cassandra.typesafety.keytypes.ColumnIndexType;

public class TimeUUIDIndex implements ColumnIndexType<TimeUUID, TimeUUIDStreamer> {

}
