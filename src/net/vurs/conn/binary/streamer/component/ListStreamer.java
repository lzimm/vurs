package net.vurs.conn.binary.streamer.component;

import java.lang.reflect.Constructor;
import java.lang.reflect.ParameterizedType;
import java.util.ArrayList;
import java.util.List;

import net.vurs.common.DeltaTrackingList;
import net.vurs.common.TimeUUID;
import net.vurs.conn.binary.BinaryConnection;
import net.vurs.conn.binary.BinaryPayload;
import net.vurs.conn.binary.streamer.BinaryComponentStreamer;
import net.vurs.service.definition.EntityService;
import net.vurs.util.ErrorControl;
import net.vurs.util.Serialization;

public class ListStreamer<T> extends BinaryComponentStreamer<List<T>> {

	private Class<T> listType = null;
	private BinaryComponentStreamer<T> subStreamer = null;
	
	@SuppressWarnings("unchecked")
	public ListStreamer(EntityService service, Class<List<T>> type,
			ParameterizedType subType) {
		super(service, type, subType);
		ParameterizedType pType = (ParameterizedType) subType.getActualTypeArguments()[0];
		this.listType = (Class<T>) pType.getRawType();
		
		try {			
			Class<? extends BinaryComponentStreamer<?>> cls = this.service.getStreamer(BinaryConnection.class, BinaryComponentStreamer.class, this.listType);
			Constructor<? extends BinaryComponentStreamer<?>> ctor = cls.getConstructor(EntityService.class, Class.class, ParameterizedType.class);
			this.subStreamer = (BinaryComponentStreamer<T>) ctor.newInstance(this.service, type, pType);
		} catch (Exception e) {
			ErrorControl.logException(e);
		}
	}

	@Override
	public List<T> read(BinaryPayload data) {
		DeltaTrackingList<T> list = new DeltaTrackingList<T>();
		
		int count = Serialization.deserializeInt(data.read(4));
		while (0 < count--) {
			list.add(this.subStreamer.read(data), TimeUUID.fromBytes(data.getName()));
		}
		
		list.mark();
		
		return list;
	}

	@SuppressWarnings("unchecked")
	@Override
	public BinaryPayload write(Object comp, byte[] name, long ts) {
		List<byte[]> list = new ArrayList<byte[]>(((DeltaTrackingList<T>) comp).size());
		list.add(Serialization.serialize(list.size()));
		
		int len = 4;
		for (T t: ((DeltaTrackingList<T>) comp)) {
			BinaryPayload sub = this.subStreamer.write(t, name, ts);
			byte[] bytes = sub.getValue();
			
			list.add(bytes);
			len += bytes.length;
		}
		
		return BinaryPayload.fromList(name, ts, len, list);
	}

}
