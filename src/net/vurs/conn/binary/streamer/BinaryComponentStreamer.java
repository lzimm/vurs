package net.vurs.conn.binary.streamer;

import java.lang.reflect.ParameterizedType;

import net.vurs.conn.binary.BinaryPayload;
import net.vurs.entity.ComponentStreamer;
import net.vurs.service.definition.EntityService;

public abstract class BinaryComponentStreamer<C> extends ComponentStreamer<C, BinaryPayload, BinaryPayload> {

	protected EntityService service = null;
	protected Class<C> type = null;
	protected ParameterizedType subType = null;
	
	public BinaryComponentStreamer(EntityService service, Class<C> type, ParameterizedType subType) {
		this.service = service;
		this.type = type;
		this.subType = subType;
	}

}
