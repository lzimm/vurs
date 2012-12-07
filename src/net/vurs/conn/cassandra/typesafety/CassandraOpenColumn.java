package net.vurs.conn.cassandra.typesafety;

import net.vurs.conn.cassandra.typesafety.keytypes.ColumnIndexType;

public interface CassandraOpenColumn<K, C extends ColumnIndexType<K, ?>, V> extends CassandraBackedDefinition {

}
