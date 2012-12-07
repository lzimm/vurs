package net.vurs.entity;

import java.util.LinkedList;
import java.util.List;

import net.vurs.common.Triple;
import net.vurs.transaction.Transaction;

public abstract class Operation<R, V> {

	public enum State {
		PENDING,
		PREPROCESSING,
		POSTPROCESSING,
		SUCCESS,
		FAILURE
	}
	
	protected Transaction transaction;
	
	protected State state;
	protected R result;
	protected V value;
	
	protected List<Operation<?, ?>> preDependencies;
	protected List<Operation<?, ?>> postDependencies;
	
	public Operation(Transaction transaction) {
		this.bind(transaction);
		this.state = State.PENDING;
	}

	public Transaction getTransaction() { return this.transaction; }
	public void setTransaction(Transaction transaction) { this.transaction = transaction; }

	public Transaction bind(Transaction transaction) {
		Transaction oldTransaction = this.transaction;
		
		if (oldTransaction != null) {
			oldTransaction.remove(this);
		}
		
		this.transaction = transaction;
		this.transaction.add(this);
		
		return oldTransaction;
	}
	
	public Operation<R, V> addPreDependency(Operation<?, ?> op) { this.preDependencies = this.addDependency(this.preDependencies, op); return this; }
	public Operation<R, V> addPostDependency(Operation<?, ?> op) { this.postDependencies = this.addDependency(this.postDependencies, op); op.addPreDependency(this); return this; }
	public List<Operation<?, ?>> addDependency(List<Operation<?, ?>> ops, Operation<?, ?> op) {
		if (ops == null) {
			ops = new LinkedList<Operation<?, ?>>();
		}
		
		ops.add(op);
		
		return ops;
	}
	
	public void execute() {
		this.transaction.begin(this);
		
		if (!this.processDependencies(State.PREPROCESSING, this.preDependencies)) return;
		
		Triple<Boolean, R, V> r = this.op();
		this.result = r.b();
		this.value = r.c();
				
		if (!this.processDependencies(State.POSTPROCESSING, this.postDependencies)) return;
		
		if (r.a()) {
			this.state = State.SUCCESS;
			this.transaction.success(this);
		} else {
			this.state = State.FAILURE;
			this.transaction.failure(this);
		}
	}

	protected boolean processDependencies(State state, List<Operation<?, ?>> ops) {
		this.state = state;
		
		if (ops != null) {
			for (Operation<?, ?> op: ops) {
				if (op.getState(true).equals(State.FAILURE)) {
					this.state = State.FAILURE;
					this.transaction.failure(this);
					return false;
				}
			}
		}
		
		return true;
	}

	public State getState() { return this.getState(false); }
	public State getState(boolean execute) {
		if (execute && this.state.equals(State.PENDING)) {
			this.execute();
		}
		
		return this.state;
	}
	
	public R getResult() { 
		if (this.state.equals(State.PENDING)) {
			this.execute();
		}
		
		return this.result;
	}
	
	public V getValue() {
		if (this.state.equals(State.PENDING)) {
			this.execute();
		}
		
		return this.value;
	}
	
	protected abstract Triple<Boolean, R, V> op();
	
}
