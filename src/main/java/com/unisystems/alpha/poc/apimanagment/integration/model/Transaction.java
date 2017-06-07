package com.unisystems.alpha.poc.apimanagment.integration.model;

public class Transaction {
	
	private String id;
	private Account this_account;
	private Account other_account;
	private TransactionDetails details;
	
	
	
	public Transaction() {
		super();
		// TODO Auto-generated constructor stub
	}

	public Transaction(String id, Account this_account, Account other_account, TransactionDetails details) {
		super();
		this.id = id;
		this.this_account = this_account;
		this.other_account = other_account;
		this.details = details;
	}
	
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public Account getThis_account() {
		return this_account;
	}
	public void setThis_account(Account this_account) {
		this.this_account = this_account;
	}
	public Account getOther_account() {
		return other_account;
	}
	public void setOther_account(Account other_account) {
		this.other_account = other_account;
	}
	public TransactionDetails getDetails() {
		return details;
	}
	public void setDetails(TransactionDetails details) {
		this.details = details;
	}
	
	
}
