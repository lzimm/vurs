package net.vurs.conn.cassandra.streamer.component;

import java.lang.reflect.Constructor;
import java.lang.reflect.ParameterizedType;
import java.util.ArrayList;
import java.util.List;

import org.apache.cassandra.thrift.Column;
import org.apache.cassandra.thrift.ColumnOrSuperColumn;
import org.apache.cassandra.thrift.SuperColumn;

import net.vurs.common.DeltaTrackingList;
import net.vurs.common.Pair;
import net.vurs.common.TimeUUID;
import net.vurs.conn.cassandra.CassandraConnection;
import net.vurs.conn.cassandra.streamer.CassandraComponentStreamer;
import net.vurs.service.definition.EntityService;
import net.vurs.util.ErrorControl;

public class ListStreamer<T> extends CassandraComponentStreamer<List<T>> {

	private Class<T> listType = null;
	private CassandraComponentStreamer<T> subStreamer = null;
	
	@SuppressWarnings("unchecked")
	public ListStreamer(EntityService service, Class<List<T>> type,
			ParameterizedType subType) {
		super(service, type, subType);
		
		ParameterizedType pType = (ParameterizedType) subType.getActualTypeArguments()[0];
		this.listType = (Class<T>) pType.getRawType();
		
		try {			
			Class<? extends CassandraComponentStreamer<?>> cls = this.service.getStreamer(CassandraConnection.class, CassandraComponentStreamer.class, this.listType);
			Constructor<? extends CassandraComponentStreamer<?>> ctor = cls.getConstructor(EntityService.class, Class.class, ParameterizedType.class);
			this.subStreamer = (CassandraComponentStreamer<T>) ctor.newInstance(this.service, type, pType);
		} catch (Exception e) {
			ErrorControl.logException(e);
		}
	}

	@Override
	public List<T> read(ColumnOrSuperColumn data) {
		DeltaTrackingList<T> list = new DeltaTrackingList<T>();
		
		for (Column col: data.super_column.columns) {
			ColumnOrSuperColumn c = new ColumnOrSuperColumn();
			c.column = col;
			list.add(this.subStreamer.read(c), TimeUUID.fromBytes(col.name));
		}
		
		list.mark();
		
		return list;
	}

	@SuppressWarnings("unchecked")
	@Override
	public ColumnOrSuperColumn write(Object comp, byte[] name, long ts) {
		ColumnOrSuperColumn c = new ColumnOrSuperColumn();
		
		List<Column> cols = new ArrayList<Column>();
		for (Pair<TimeUUID, T> pair: ((DeltaTrackingList<T>) comp).getAdditions()) {
			cols.add(this.subStreamer.write(pair.b(), pair.a().getBytes(), ts).column);
		}
		
		c.super_column = new SuperColumn(name, cols);
		return c;
	}

}
