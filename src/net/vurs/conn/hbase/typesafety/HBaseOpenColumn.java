package net.vurs.conn.hbase.typesafety;

import net.vurs.conn.hbase.typesafety.keytypes.ColumnIndexType;

public interface HBaseOpenColumn<K, C extends ColumnIndexType<K, ?>, V> extends HBaseBackedDefinition {

}
