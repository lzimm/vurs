package net.vurs.common;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

public class FutureResponse<T> implements Future<T> {

	private CountDownLatch latch = null;
	private T val = null;
	
	public FutureResponse() {
		this.latch = new CountDownLatch(1);
	}

	public void set(T val) {
		this.val = val;
		this.latch.countDown();
	}
	
	@Override
	public boolean cancel(boolean mayInterruptIfRunning) {
		return false;
	}

	@Override
	public T get() throws InterruptedException, ExecutionException {
		this.latch.await();
		return this.val;
	}

	@Override
	public T get(long timeout, TimeUnit unit) throws InterruptedException,
			ExecutionException, TimeoutException {
		this.latch.await(timeout, unit);
		return this.val;
	}

	@Override
	public boolean isCancelled() {
		return false;
	}

	@Override
	public boolean isDone() {
		return false;
	}
	
}
