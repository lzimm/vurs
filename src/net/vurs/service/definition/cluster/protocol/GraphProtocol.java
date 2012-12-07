package net.vurs.service.definition.cluster.protocol;

import java.nio.ByteBuffer;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Future;

import net.vurs.common.FutureResponse;
import net.vurs.common.ImmediateFuture;
import net.vurs.service.EventDispatcher;
import net.vurs.service.definition.ClusterService;
import net.vurs.service.definition.cluster.ClusterNode;
import net.vurs.service.definition.cluster.ClusterProtocol;
import net.vurs.service.definition.cluster.socket.SocketConnection;

public class GraphProtocol extends ClusterProtocol {

	private ConcurrentHashMap<Long, FutureResponse<Object>> responses = null;
	
	public GraphProtocol(EventDispatcher eventDispatcher, ClusterService clusterService, Integer port) {
		super(eventDispatcher, clusterService, port);
		this.responses = new ConcurrentHashMap<Long, FutureResponse<Object>>();
	}

	@Override
	public Future<byte[]> onRequest(byte[] request) {
		return new ImmediateFuture<byte[]>(request);
	}

	@Override
	public Future<byte[]> onResponse(byte[] response) {
		ByteBuffer buffer = ByteBuffer.wrap(response);
		
		long reqID = buffer.getLong();
		byte[] bytes = new byte[buffer.remaining()];
		buffer.get(bytes);
		
		this.responses.get(reqID).set(new String(bytes));
		
		return new ImmediateFuture<byte[]>(response);
	}
	
	public FutureResponse<Object> test(String key) {
		long reqID = this.requestCounter.getAndIncrement();
		
		ClusterNode node = this.heartbeat.findPosition(key.hashCode());
		SocketConnection connection = this.connectionPool.getConnection(node);
		
		ByteBuffer buffer = ByteBuffer.allocate(8 + key.getBytes().length);
		
		buffer.putLong(reqID);
		buffer.put(key.getBytes());
		
		FutureResponse<Object> ret = new FutureResponse<Object>();
		this.responses.put(reqID, ret);
		
		connection.send(buffer.array());
		
		return ret;
	}

}
