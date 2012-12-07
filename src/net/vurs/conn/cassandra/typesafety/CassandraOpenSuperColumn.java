package net.vurs.conn.cassandra.typesafety;

import net.vurs.conn.cassandra.typesafety.keytypes.ColumnIndexType;

public interface CassandraOpenSuperColumn<S, A extends ColumnIndexType<S, ?>, K, B extends ColumnIndexType<K, ?>, V> extends CassandraBackedDefinition {

}