package net.vurs.conn.binary.streamer.component;

import java.lang.reflect.Constructor;
import java.lang.reflect.ParameterizedType;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import net.vurs.conn.binary.BinaryConnection;
import net.vurs.conn.binary.BinaryPayload;
import net.vurs.conn.binary.streamer.BinaryComponentStreamer;
import net.vurs.service.definition.EntityService;
import net.vurs.util.ErrorControl;
import net.vurs.util.Serialization;

public class MapStreamer<V> extends BinaryComponentStreamer<Map<String, V>> {

	private Class<V> valueType = null;
	private BinaryComponentStreamer<String> keyStreamer = null;
	private BinaryComponentStreamer<V> valueStreamer = null;
	
	@SuppressWarnings("unchecked")
	public MapStreamer(EntityService service, Class<Map<String, V>> type,
			ParameterizedType subType) {
		super(service, type, subType);
		
		ParameterizedType kType = (ParameterizedType) subType.getActualTypeArguments()[0];
		this.keyStreamer = new StringStreamer(this.service, String.class, kType);
		
		ParameterizedType vType = (ParameterizedType) subType.getActualTypeArguments()[1];
		this.valueType = (Class<V>) vType.getRawType();
		
		try {			
			Class<? extends BinaryComponentStreamer<?>> vCls = this.service.getStreamer(BinaryConnection.class, BinaryComponentStreamer.class, this.valueType);
			Constructor<? extends BinaryComponentStreamer<?>> vCtor = vCls.getConstructor(EntityService.class, Class.class, ParameterizedType.class);
			this.valueStreamer = (BinaryComponentStreamer<V>) vCtor.newInstance(this.service, type, vType);
		} catch (Exception e) {
			ErrorControl.logException(e);
		}
	}

	@Override
	public Map<String, V> read(BinaryPayload data) {
		Map<String, V> ret = new HashMap<String, V>();
		
		int count = Serialization.deserializeInt(data.read(4));
		while (0 < count--) {
			String key = this.keyStreamer.read(data);
			ret.put(key, this.valueStreamer.read(data));
		}
		
		return ret;
	}

	@SuppressWarnings("unchecked")
	@Override
	public BinaryPayload write(Object comp, byte[] name, long ts) {
		List<byte[]> list = new ArrayList<byte[]>(((Map<String, V>) comp).size());
		list.add(Serialization.serialize(list.size()));
		
		int len = 4;
		for (Entry<String, V> entry: ((Map<String, V>) comp).entrySet()) {
			BinaryPayload key = this.keyStreamer.write(entry.getKey(), name, ts);
			byte[] keyBytes = key.getValue();
			
			list.add(keyBytes);
			len += keyBytes.length;
			
			BinaryPayload value = this.valueStreamer.write(entry.getValue(), name, ts);
			byte[] valueBytes = value.getValue();
			
			list.add(valueBytes);
			len += valueBytes.length;
		}
		
		return BinaryPayload.fromList(name, ts, len, list);
	}

}
