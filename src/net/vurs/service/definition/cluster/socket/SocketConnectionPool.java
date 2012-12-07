package net.vurs.service.definition.cluster.socket;

import java.net.InetAddress;
import java.util.concurrent.ConcurrentHashMap;

import net.vurs.service.definition.cluster.ClusterNode;
import net.vurs.service.definition.cluster.ClusterProtocol;
import net.vurs.util.ErrorControl;

public class SocketConnectionPool {

	private ConcurrentHashMap<InetAddress, SocketConnection> connections = null;
	private ClusterProtocol protocol = null;
	private int port = -1;
	private Thread ioThread = null;
	
	public SocketConnectionPool(ClusterProtocol protocol, int port) {
		this.protocol = protocol;
		this.port = port;
		this.connections = new ConcurrentHashMap<InetAddress, SocketConnection>();
		
		this.ioThread = startIO(connections);
		this.ioThread.setDaemon(true);
		this.ioThread.setName(getClass().getSimpleName() + ": " + this.protocol.getClass().getSimpleName());
		this.ioThread.start();
	}
	
	private Thread startIO(final ConcurrentHashMap<InetAddress, SocketConnection> connections) {
		Thread thread = new Thread(new Runnable() {

			@Override
			public void run() {
				while (true) {
					boolean doneWork = false;
					for (SocketConnection conn: connections.values()) {
						if (conn.handle()) {
							doneWork = true;
						}
					}
					
					if (!doneWork) {
						try {
								Thread.sleep(10);
						} catch (Exception e) {
							ErrorControl.logException(e);
						}
					}
				}
			}
			
		});
		
		return thread;
	}

	public SocketConnection getConnection(ClusterNode node) {
		InetAddress address = node.getAddress();
		SocketConnection conn = this.connections.get(address);
		
		if (conn == null) {
			conn = new SocketConnection(this, this.protocol, address, this.port);
			if (this.connections.putIfAbsent(address, conn) != null) {
				conn.close();
				conn = this.connections.get(address);
			}
		}
		
		if (!conn.isConnected()) {
			conn.close();
			return getConnection(node);
		}
		
		return conn;
	}
	
	protected boolean removeConnection(SocketConnection connection) {
		return this.connections.remove(connection.getAddress(), connection);
	}
	
}
