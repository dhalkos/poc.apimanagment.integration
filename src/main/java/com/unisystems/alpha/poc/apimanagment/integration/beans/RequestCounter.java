package com.unisystems.alpha.poc.apimanagment.integration.beans;

public class RequestCounter {

	private static int balanceRequestCnt = 1, transactionsRequestCnt = 1, transferRequestCnt = 1;
	
	public String getBalanceRequestId() {		
		return "bln-req-" + balanceRequestCnt++;
	}
	
	public String getTransactionsRequestId() {
		return "trct-req-" + transactionsRequestCnt++;
	}
	
	public String getTransferRequestId() {
		return "trsf-req-" + transferRequestCnt++;
	}
}
