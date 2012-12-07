package net.vurs.conn.cassandra.streamer.component;

import java.lang.reflect.Constructor;
import java.lang.reflect.ParameterizedType;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.cassandra.thrift.Column;
import org.apache.cassandra.thrift.ColumnOrSuperColumn;
import org.apache.cassandra.thrift.SuperColumn;

import net.vurs.conn.cassandra.CassandraConnection;
import net.vurs.conn.cassandra.streamer.CassandraComponentStreamer;
import net.vurs.service.definition.EntityService;
import net.vurs.util.ErrorControl;
import net.vurs.util.Serialization;

public class MapStreamer<V> extends CassandraComponentStreamer<Map<String, V>> {

	private Class<V> valueType = null;
	private CassandraComponentStreamer<V> valueStreamer = null;
	
	@SuppressWarnings("unchecked")
	public MapStreamer(EntityService service, Class<Map<String, V>> type,
			ParameterizedType subType) {
		super(service, type, subType);
		
		ParameterizedType vType = (ParameterizedType) subType.getActualTypeArguments()[1];
		this.valueType = (Class<V>) vType.getRawType();
		
		try {			
			Class<? extends CassandraComponentStreamer<?>> vCls = this.service.getStreamer(CassandraConnection.class, CassandraComponentStreamer.class, this.valueType);
			Constructor<? extends CassandraComponentStreamer<?>> vCtor = vCls.getConstructor(EntityService.class, Class.class, ParameterizedType.class);
			this.valueStreamer = (CassandraComponentStreamer<V>) vCtor.newInstance(this.service, type, vType);
		} catch (Exception e) {
			ErrorControl.logException(e);
		}
	}

	@Override
	public Map<String, V> read(ColumnOrSuperColumn data) {
		Map<String, V> ret = new HashMap<String, V>();
		
		for (Column col: data.super_column.columns) {
			ColumnOrSuperColumn c = new ColumnOrSuperColumn();
			c.column = col;
			
			ret.put(Serialization.deserializeString(col.name), this.valueStreamer.read(c));
		}
		
		return ret;
	}

	@SuppressWarnings("unchecked")
	@Override
	public ColumnOrSuperColumn write(Object comp, byte[] name, long ts) {
		ColumnOrSuperColumn c = new ColumnOrSuperColumn();
		
		List<Column> cols = new ArrayList<Column>();
		for (Entry<String, V> e: ((Map<String, V>) comp).entrySet()) {			
			cols.add(this.valueStreamer.write(e.getValue(), Serialization.serialize(e.getKey()), ts).column);
		}
		
		c.super_column = new SuperColumn(name, cols);
		return c;
	}

}
