package net.vurs.service.definition.cluster.socket;

import java.net.ServerSocket;
import java.net.Socket;
import java.util.Iterator;
import java.util.concurrent.ArrayBlockingQueue;

import net.vurs.service.definition.cluster.ClusterProtocol;
import net.vurs.util.ErrorControl;

public class SocketAcceptor {
	protected static final int BUFFER_SIZE = 10*1024;
	protected static final int QUEUE_SIZE = 10;
	
	protected ClusterProtocol protocol = null;
	protected SocketWorkerPool pool = null;
	
	protected ServerSocket acceptorSocket = null;
	protected ArrayBlockingQueue<SocketHandler> handlers = null;
	
	protected Thread acceptorThread = null;
	protected Thread ioThread = null;
	
	public SocketAcceptor(ClusterProtocol protocol, SocketWorkerPool pool, int port) {
		try {
			this.protocol = protocol;
			this.pool = pool;
			
			this.handlers = new ArrayBlockingQueue<SocketHandler>(QUEUE_SIZE);
			
			this.acceptorSocket = new ServerSocket(port);
			this.acceptorSocket.setReceiveBufferSize(BUFFER_SIZE);
			this.acceptorSocket.setReuseAddress(true);
			
			this.acceptorThread = startAcceptor(acceptorSocket, pool, handlers, protocol);
			this.ioThread = startIO(handlers);
			
			this.acceptorThread.setDaemon(true);
			this.acceptorThread.setName(getClass().getSimpleName() + " (acceptor): " + this.protocol.getClass().getSimpleName());
			
			this.ioThread.setDaemon(true);
			this.ioThread.setName(getClass().getSimpleName() + " (listener): " + this.protocol.getClass().getSimpleName());
		} catch (Exception e) {
			ErrorControl.logException(e);
		}
	}
	
	private Thread startAcceptor(
			final ServerSocket acceptorSocket, 
			final SocketWorkerPool pool, 
			final ArrayBlockingQueue<SocketHandler> handlers,
			final ClusterProtocol protocol) {
		
		Thread thread = new Thread(new Runnable() {

			@Override
			public void run() {
				while (true) {
					try {
						Socket socket = acceptorSocket.accept();
						socket.setSendBufferSize(BUFFER_SIZE);
						socket.setReceiveBufferSize(BUFFER_SIZE);
						socket.setTcpNoDelay(true);
						
						SocketHandler handler = new SocketHandler(socket, pool, protocol);
						
						handlers.add(handler);
					} catch (Exception e) {
						ErrorControl.logException(e);
					}
				}
			}
			
		});
		
		return thread;
	}
	
	private Thread startIO(final ArrayBlockingQueue<SocketHandler> handlers) {
		Thread thread = new Thread(new Runnable() {

			@Override
			public void run() {
				while (true) {
					boolean doneWork = false;
					Iterator<SocketHandler> iter = handlers.iterator();
					while (iter.hasNext()) {
						SocketHandler handler = iter.next();
						if (handler.isConnected()) {
							if (handler.handle()) {
								doneWork = true;
							}
						} else {
							iter.remove();
							handler.close();
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
	
	public void start() {
		this.acceptorThread.start();
		this.ioThread.start();
	}
	
}
