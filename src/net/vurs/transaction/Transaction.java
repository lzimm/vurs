package net.vurs.transaction;

import java.util.HashSet;

import net.vurs.entity.Operation;
import net.vurs.service.definition.TransactionService;

public class Transaction {

	private TransactionService transactionService;
	private HashSet<Operation<?, ?>> operations;
	
	public Transaction(TransactionService transactionService) {
		this.transactionService = transactionService;
		this.operations = new HashSet<Operation<?, ?>>();
	}
	
	public void add(Operation<?, ?> op) {
		if (op.getTransaction().equals(this)) {
			this.operations.add(op);
		} else {
			op.bind(this);
		}
	}
	
	public void remove(Operation<?, ?> op) {
		this.operations.remove(op);
		op.setTransaction(null);
	}
	
	public void success(Operation<?, ?> op) {
		this.operations.remove(op);
	}
	
	public void failure(Operation<?, ?> op) {
		this.operations.remove(op);
	}
	
	public void success() {
	}

	public void failure() {
	}

	public void finish() {
	}
	
	public void begin(Operation<?, ?> op) {
		this.transactionService.addAutomaticDependencies(this, op);
	}
 
}
