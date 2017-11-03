package com.hungle.tools.moneyutils.yahoo;

import java.util.Date;

public class YahooScreenScrapper2StockInfo {
	private Double price;
	
	private String symbol;
	
	private String name;
	
	private String currency;
	
	private Date lastTrade;
	
	public YahooScreenScrapper2StockInfo() {
		// TODO Auto-generated constructor stub
	}

	public String getSymbol() {
		return symbol;
	}

	public void setSymbol(String symbol) {
		this.symbol = symbol;
	}

	public Double getPrice() {
		return price;
	}

	public void setPrice(Double price) {
		this.price = price;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getCurrency() {
		return currency;
	}

	public void setCurrency(String currency) {
		this.currency = currency;
	}

	public Date getLastTrade() {
		return lastTrade;
	}

	public void setLastTrade(Date lastTrade) {
		this.lastTrade = lastTrade;
	}

	@Override
	public String toString() {
		return "YahooScreenScrapper2StockInfo [symbol=" + symbol + ", price=" + price + "]";
	}

}
