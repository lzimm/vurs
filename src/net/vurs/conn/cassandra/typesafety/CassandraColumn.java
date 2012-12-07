package net.vurs.conn.cassandra.typesafety;

import net.vurs.conn.cassandra.typesafety.keytypes.ColumnIndexType;

public interface CassandraColumn<K, C extends ColumnIndexType<K, ?>> extends CassandraBackedDefinition {

}
