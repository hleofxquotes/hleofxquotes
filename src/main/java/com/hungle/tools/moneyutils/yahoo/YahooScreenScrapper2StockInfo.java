package com.hungle.tools.moneyutils.yahoo;

public class YahooScreenScrapper2StockInfo {
	@Override
	public String toString() {
		return "YahooScreenScrapper2StockInfo [symbol=" + symbol + ", price=" + price + "]";
	}

	private String symbol;
	
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

	private Double price;
	
	public YahooScreenScrapper2StockInfo() {
		// TODO Auto-generated constructor stub
	}

}
