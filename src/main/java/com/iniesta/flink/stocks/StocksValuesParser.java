package com.iniesta.flink.stocks;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;


public class StocksValuesParser {

	Map<String, Double> stocks = new HashMap<>();
	private List<String> symbols = new ArrayList<>(); 
	private Random random;

	public StocksValuesParser(String market) {
		loadMarketFile(market);
		random = new Random();
	}

	private void loadMarketFile(String market) {
		String fileName = getClass().getClassLoader().getResource(market + ".csv").getPath();
		BufferedReader br = null;
		try {
			br = new BufferedReader(new FileReader(fileName));
			br.readLine();
			String line = null;
			while ((line = br.readLine()) != null) {
				String[] fields = line.split(",");
				try {
					Double lastSale = new Double(fields[1]);
					stocks.put(fields[0], lastSale);
					symbols.add(fields[0]);
				} catch (NumberFormatException e) {
				}
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {
			try {
				br.close();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}
	
	public synchronized StockTrade getStockTrade(){
		StockTrade trade = new StockTrade();
		String symbol = symbols.get(random.nextInt(symbols.size()));
		trade.setSymbol(symbol);
		trade.setNum(random.nextInt(10000)+1);
		double currentPrice = stocks.get(symbol);
		boolean isSell = random.nextBoolean();
		double skew = currentPrice * random.nextDouble();
		double newPrice = currentPrice + (isSell?skew:-skew);
		trade.setPrice(newPrice);
		stocks.put(symbol, newPrice);
		trade.setTimestamp(new Date());		
		trade.setType(isSell?"SELL":"BUY");
		return trade;
	}
}
