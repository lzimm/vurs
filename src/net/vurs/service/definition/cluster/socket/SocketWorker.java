package net.vurs.service.definition.cluster.socket;

import java.util.Collection;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.Future;

import net.vurs.common.Pair;
import net.vurs.service.definition.cluster.ClusterProtocol;
import net.vurs.util.ErrorControl;

public class SocketWorker {
	protected final int QUEUE_SIZE = 32;
	
	protected SocketWorkerPool pool = null;
	protected ClusterProtocol protocol = null;
	
	protected ArrayBlockingQueue<Pair<SocketHandler, byte[]>> requests = null;
	protected Thread workerThread = null;

	public SocketWorker(SocketWorkerPool pool, ClusterProtocol protocol) {
		this.pool  = pool;
		this.protocol = protocol;
		this.requests = new ArrayBlockingQueue<Pair<SocketHandler, byte[]>>(QUEUE_SIZE);
		
		this.workerThread = startWorker(this, this.protocol, this.requests);
		this.workerThread.setName(getClass().getSimpleName() + ": " + this.protocol.getClass().getSimpleName());
		this.workerThread.start();
	}
	
	private Thread startWorker(
			final SocketWorker worker, 
			final ClusterProtocol protocol,
			final ArrayBlockingQueue<Pair<SocketHandler, byte[]>> requests) {
		
		Thread thread = new Thread(new Runnable() {

			@Override
			public void run() {
				while (!requests.isEmpty()) {
					Pair<SocketHandler, byte[]> pair = requests.poll();
					Future<byte[]> response = protocol.onRequest(pair.b());
					worker.respond(pair.a(), response);
				}
				
				try {
					synchronized(requests) {
						requests.wait();
					}
				} catch (Exception e) {
					ErrorControl.logException(e);
				}
			}
			
		});
		
		return thread;
	}
	
	public void respond(SocketHandler handler, Future<byte[]> response) {
		this.pool.addResponse(handler, response);
	}
	
	public void release() {
		this.pool.release(this);
	}

	public void process(Collection<Pair<SocketHandler, byte[]>> requests) {
		this.requests.addAll(requests);
		synchronized(this.requests) {
			this.requests.notify();
		}
	}

	public int getQueueSize() {
		return QUEUE_SIZE;
	}
	
}
