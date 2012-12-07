package net.vurs.conn.cassandra.typesafety;

import net.vurs.conn.cassandra.typesafety.keytypes.ColumnIndexType;

public interface CassandraSuperColumn<S, A extends ColumnIndexType<S, ?>, K, B extends ColumnIndexType<K, ?>> extends CassandraBackedDefinition {

}
