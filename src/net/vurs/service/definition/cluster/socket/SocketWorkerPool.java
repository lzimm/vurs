package net.vurs.service.definition.cluster.socket;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.Future;

import net.vurs.common.Pair;
import net.vurs.service.definition.cluster.ClusterProtocol;
import net.vurs.util.ErrorControl;

public class SocketWorkerPool {
	protected final int QUEUE_SIZE = 128;
	protected final int FUTURE_MULTIPLE = 12;
	
	protected ClusterProtocol protocol = null;
	protected ArrayBlockingQueue<Pair<SocketHandler, byte[]>> queue = null;
	protected ArrayBlockingQueue<Pair<SocketHandler, Future<byte[]>>> futures = null;
	protected ConcurrentHashMap<SocketHandler, ArrayBlockingQueue<byte[]>> mailboxes = null;
	
	protected Thread schedulerThread = null;
	protected ConcurrentLinkedQueue<SocketWorker> pool = null;
	
	public SocketWorkerPool(ClusterProtocol protocol) {
		this.protocol = protocol;
		this.queue = new ArrayBlockingQueue<Pair<SocketHandler, byte[]>>(QUEUE_SIZE);
		this.futures = new ArrayBlockingQueue<Pair<SocketHandler, Future<byte[]>>>(QUEUE_SIZE * FUTURE_MULTIPLE);
		this.pool = new ConcurrentLinkedQueue<SocketWorker>();
		this.mailboxes = new ConcurrentHashMap<SocketHandler, ArrayBlockingQueue<byte[]>>();
		
		this.schedulerThread = startScheduler(this, this.queue, this.futures, this.mailboxes);
		this.schedulerThread.setDaemon(true);
		this.schedulerThread.setName(getClass().getSimpleName() + ": " + this.protocol.getClass().getSimpleName());
		this.schedulerThread.start();
	}
	
	private Thread startScheduler(
			final SocketWorkerPool pool,
			final ArrayBlockingQueue<Pair<SocketHandler, byte[]>> queue,
			final ArrayBlockingQueue<Pair<SocketHandler, Future<byte[]>>> futures,
			final ConcurrentHashMap<SocketHandler, ArrayBlockingQueue<byte[]>> mailboxes) {

		Thread thread = new Thread(new Runnable() {

			@Override
			public void run() {
				while (true) {
					boolean doneWork = false;
					if (queue.size() > 0) {
						SocketWorker worker = pool.acquire();
						ArrayList<Pair<SocketHandler, byte[]>> requests = new ArrayList<Pair<SocketHandler, byte[]>>(worker.getQueueSize());
						queue.drainTo(requests, worker.getQueueSize());
						worker.process(requests);
						doneWork = true;
					}
					
					if (futures.size() > 0) {
						Iterator<Pair<SocketHandler, Future<byte[]>>> itr = futures.iterator();
						while (itr.hasNext()) {
							Pair<SocketHandler, Future<byte[]>> pair = itr.next();
							if (pair.b().isDone()) {
								itr.remove();
								try {
									ArrayBlockingQueue<byte[]> mailbox = mailboxes.get(pair.a());
									mailbox.add(pair.b().get());
								} catch (Exception e) {
									ErrorControl.logException(e);
								}
							}
						}
						
						if (futures.size() > 0) {
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

	public SocketWorker acquire() {
		SocketWorker worker = pool.poll();
		
		if (worker == null) {
			worker = create();
		}
		
		return worker;
	}
	
	private SocketWorker create() {
		SocketWorker instance = null;
		
		try {
			instance = new SocketWorker(this, this.protocol);
		} catch (Exception e) {
			ErrorControl.logException(e);
		}
		
		return instance;
	}
	
	public void release(SocketWorker worker) {
		this.pool.add(worker);
	}
	
	public void addResponse(SocketHandler handler, Future<byte[]> response) {
		if (response.isDone()) {
			try {
				ArrayBlockingQueue<byte[]> mailbox = this.mailboxes.get(handler);
				mailbox.add(response.get());
			} catch (Exception e) {
				ErrorControl.logException(e);
			}
		} else {
			this.futures.add(new Pair<SocketHandler, Future<byte[]>>(handler, response));
		}
	}

	public void addRequest(SocketHandler handler, byte[] request) {
		this.queue.add(new Pair<SocketHandler, byte[]>(handler, request));
	}
	
	public void addHandler(SocketHandler handler, ArrayBlockingQueue<byte[]> queue) {
		this.mailboxes.put(handler, queue);
	}
	
	public void removeHandler(SocketHandler handler) {
		this.mailboxes.remove(handler);
	}
	
}
