package net.vurs.conn.hbase.typesafety.keytypes.column;

import net.vurs.common.GeoPoint;
import net.vurs.conn.hbase.streamer.component.GeoPointStreamer;
import net.vurs.conn.hbase.typesafety.keytypes.ColumnIndexType;

public class GeoPointIndex implements ColumnIndexType<GeoPoint, GeoPointStreamer> {

}
