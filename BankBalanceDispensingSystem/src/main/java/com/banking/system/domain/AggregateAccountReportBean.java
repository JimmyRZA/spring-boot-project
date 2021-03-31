package com.banking.system.domain;

import org.springframework.stereotype.Component;

@Component
public class AggregateAccountReportBean {
	private String clientTitle;
	private String clientName;
	private String clientSurname;
	private float transactionalBalance;
	private float netPosition;
	
	public AggregateAccountReportBean() {
		
	}

	public AggregateAccountReportBean(String clientTitle, String clientName, String clientSurname,
			float transactionalBalance, float netPosition) {
		super();
		this.clientTitle = clientTitle;
		this.clientName = clientName;
		this.clientSurname = clientSurname;
		this.transactionalBalance = transactionalBalance;
		this.netPosition = netPosition;
	}

	public String getClientTitle() {
		return clientTitle;
	}

	public void setClientTitle(String clientTitle) {
		this.clientTitle = clientTitle;
	}

	public String getClientName() {
		return clientName;
	}

	public void setClientName(String clientName) {
		this.clientName = clientName;
	}

	public String getClientSurname() {
		return clientSurname;
	}

	public void setClientSurname(String clientSurname) {
		this.clientSurname = clientSurname;
	}

	public float getTransactionalBalance() {
		return transactionalBalance;
	}

	public void setTransactionalBalance(float transactionalBalance) {
		this.transactionalBalance = transactionalBalance;
	}

	public float getNetPosition() {
		return netPosition;
	}

	public void setNetPosition(float netPosition) {
		this.netPosition = netPosition;
	}
	
}
