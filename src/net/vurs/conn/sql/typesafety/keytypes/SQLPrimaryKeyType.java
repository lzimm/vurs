package net.vurs.conn.sql.typesafety.keytypes;

import net.vurs.conn.sql.streamer.SQLPrimaryKeyStreamer;
import net.vurs.entity.typesafety.PrimaryKeyType;

public interface SQLPrimaryKeyType<T, P extends SQLPrimaryKeyStreamer<T>> extends PrimaryKeyType<T> {

}
