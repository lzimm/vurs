package net.vurs.conn.hbase.typesafety;

import net.vurs.conn.hbase.typesafety.keytypes.ColumnIndexType;

public interface HBaseColumn<K, C extends ColumnIndexType<K, ?>> extends HBaseBackedDefinition {

}
