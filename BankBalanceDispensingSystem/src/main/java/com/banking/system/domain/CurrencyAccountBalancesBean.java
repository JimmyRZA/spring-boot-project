package com.banking.system.domain;

import org.springframework.stereotype.Component;

@Component
public class CurrencyAccountBalancesBean {
	private String accountNumber;
	private String currency;
	private float currencyBalance;
	private float conversionRate;
	private String zarAmount;
	
	public CurrencyAccountBalancesBean() {
		
	}
	
	public CurrencyAccountBalancesBean(String accountNumber, String currency, float currencyBalance,
			float conversionRate, String zarAmount) {
		super();
		this.accountNumber = accountNumber;
		this.currency = currency;
		this.currencyBalance = currencyBalance;
		this.conversionRate = conversionRate;
		this.zarAmount = zarAmount;
	}
	
	
	public String getAccountNumber() {
		return accountNumber;
	}
	public void setAccountNumber(String accountNumber) {
		this.accountNumber = accountNumber;
	}
	public String getCurrency() {
		return currency;
	}
	public void setCurrency(String currency) {
		this.currency = currency;
	}
	public float getCurrencyBalance() {
		return currencyBalance;
	}
	public void setCurrencyBalance(float currencyBalance) {
		this.currencyBalance = currencyBalance;
	}
	public double getConversionRate() {
		return conversionRate;
	}
	public void setConversionRate(float conversionRate) {
		this.conversionRate = conversionRate;
	}
	public String getZarAmount() {
		return zarAmount;
	}
	public void setZarAmount(String zarAmount) {
		this.zarAmount = zarAmount;
	}
	
	

}
