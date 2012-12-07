package net.vurs.conn.cassandra.typesafety.keytypes.column;

import net.vurs.common.GeoPoint;
import net.vurs.conn.cassandra.streamer.component.GeoPointStreamer;
import net.vurs.conn.cassandra.typesafety.keytypes.ColumnIndexType;

public class GeoPointIndex implements ColumnIndexType<GeoPoint, GeoPointStreamer> {

}
